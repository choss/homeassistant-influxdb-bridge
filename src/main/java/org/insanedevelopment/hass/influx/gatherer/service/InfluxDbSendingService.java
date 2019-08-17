package org.insanedevelopment.hass.influx.gatherer.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpec;
import org.insanedevelopment.hass.influx.gatherer.model.HassIoMetric;
import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoState;
import org.insanedevelopment.hass.influx.gatherer.repository.ConfigRepositoryImpl;
import org.insanedevelopment.hass.influx.gatherer.repository.HassIoStateChangeObserver;
import org.insanedevelopment.hass.influx.gatherer.repository.HassioRestStateClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class InfluxDbSendingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbSendingService.class);

	@Autowired
	private HassioRestStateClientImpl restClient;

	@Autowired
	private ConfigRepositoryImpl configRepository;

	@Autowired
	private InfluxDB influxdb;

	@Value("${spring.influx.database}")
	private String influxDatabaseName;

	@Value("${spring.influx.rententionpolicy}")
	private String retentionPolicyName;

	/**
	 * Config of the application says which
	 * domains/states should be captured or not
	 * Same for attributes
	 * Config is on domain or entityId (with exclusions)
	 */
	@PostConstruct
	public void init() {
		influxdb.setDatabase(influxDatabaseName);
		influxdb.setRetentionPolicy(retentionPolicyName);
		subscribeToChanges();
	}

	@Scheduled(cron = "*/${gatherer.updateinterval} * * * * *")
	public void scheduledSnapshotGathering() {
		createStateSnapshotAndSendToInflux();
	}

	public void createStateSnapshotAndSendToInflux() {
		Flux<HassIoState> repositoryResult = restClient.getAllCurrentStates();
		long currentTime = System.currentTimeMillis();
		repositoryResult
				.map(has -> addSpec(has))
				.filter(p -> p.getRight() != null)
				.doOnNext(ha -> LOGGER.debug("Matched {}", ha.getLeft()))
				.map(this::convertToMetrics)
				.filter(m -> m.isValid())
				.map(m -> convertToInfluxEvent(m, currentTime))
				.doOnNext(this::sendEventToInflux)
				.doOnError(e -> LOGGER.error("Error sending state snapshot", e))
				.subscribe();
	}

	public void subscribeToChanges() {
		restClient.subscribeToChanges(new HassIoStateChangeObserver() {

			@Override
			public void update(HassIoState oldState, HassIoState newState) {
				Pair<HassIoState, FilterPathSpec> itemWithRule = addSpec(newState);
				if (itemWithRule.getRight() != null) {
					HassIoMetric metric = convertToMetrics(itemWithRule);
					LOGGER.debug(".updateState got new state metric {}", metric);
					if (metric.isValid()) {
						Point point = convertToInfluxEvent(metric);
						sendEventToInflux(point);
					}
				}
			}

		});
	}

	private Point convertToInfluxEvent(HassIoMetric metric) {
		return convertToInfluxEvent(metric, metric.getLastUpdated().getTime());
	}

	private void sendEventToInflux(Point point) {
		try {
			influxdb.write(point);
		} catch (Exception e) {
			LOGGER.error("Error writing {} to influx", point, e);
		}
	}

	private Point convertToInfluxEvent(HassIoMetric metric, long timestampMilis) {
		Point point = Point.measurement("hassio")
				.time(timestampMilis, TimeUnit.MILLISECONDS)
				.tag(metric.getTags())
				.tag("friendly_name", metric.getFriendlyName())
				.tag("entity_id", metric.getEntityId())
				.tag("domain", metric.getDomain())
				.tag("entity_name", metric.getEntityName())
				.fields(metric.getMeasurements())
				.build();
		return point;
	}

	private Pair<HassIoState, FilterPathSpec> addSpec(HassIoState state) {
		String entityId = state.getEntityId();
		List<FilterPathSpec> configElements = configRepository.getAllConfigElements();
		for (FilterPathSpec filterPathSpec : configElements) {
			if (filterPathSpec.matches(entityId)) {
				return Pair.of(state, filterPathSpec);
			}
		}
		return Pair.of(state, null);
	}

	private HassIoMetric convertToMetrics(Pair<HassIoState, FilterPathSpec> item) {
		HassIoState state = item.getLeft();
		FilterPathSpec spec = item.getRight();

		HassIoMetric result = new HassIoMetric(state.getEntityId(), state.getLastUpdated());

		result.setFriendlyName(String.valueOf(state.getAttribute("friendly_name")));

		for (String attributeName : spec.getCaptureAttributes()) {
			Object attributeValue = state.getAttribute(attributeName);
			if (attributeValue != null) {
				result.addMeasurement(attributeName, convertDataType(attributeValue));
			}
		}

		for (String attributeName : spec.getTagAttributes()) {
			Object attributeValue = state.getAttribute(attributeName);
			if (attributeValue != null) {
				result.addTag(attributeName, attributeValue.toString());
			}
		}

		String stateName = getStateFieldName(state, spec);

		result.addMeasurement(stateName, convertDataType(state.getState()));

		return result;
	}

	private String getStateFieldName(HassIoState state, FilterPathSpec spec) {
		String stateName = "state";
		if (!StringUtils.isBlank(spec.getOverwriteStateFieldWithAttribute())) {
			Object attribute = state.getAttribute(spec.getOverwriteStateFieldWithAttribute());
			if (attribute != null && !Strings.isBlank(attribute.toString())) {
				stateName = attribute.toString();
			}
		}
		return stateName;
	}

	private Object convertDataType(Object attributeValue) {
		try {
			return new BigDecimal(attributeValue.toString());
		} catch (NumberFormatException nxe) {
			// ignore
		}
		String value = attributeValue.toString();
		if ("on".equals(value)) {
			return BigDecimal.ONE;
		}
		if ("off".equals(value)) {
			return BigDecimal.ZERO;
		}
		LOGGER.debug("Ignore field value:  " + value);
		return null;
	}

}
