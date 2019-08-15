package org.insanedevelopment.hass.influx.gatherer.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

	/**
	 * Config of the application says which
	 * domains/states should be captured or not
	 * Same for attributes
	 * Config is on domain or entityId (with exclusions)
	 */
	@PostConstruct
	public void init() {

	}

	public void getFilterAndLog() {
		Flux<HassIoState> repositoryResult = restClient.getAllCurrentStates();

		repositoryResult
				.map(has -> addSpec(has))
				.filter(p -> p.getRight() != null)
				.doOnNext(ha -> LOGGER.debug("Matched {}", ha.getLeft()))
				.map(this::convertToMetrics)
				.doOnNext(ha -> LOGGER.debug("Reporting {}", ha))
				.subscribe();
	}

	public void subscribeToChanges() {
		restClient.subscribeToChanges(new HassIoStateChangeObserver() {

			@Override
			public void update(HassIoState oldState, HassIoState newState) {
				Pair<HassIoState, FilterPathSpec> itemWithRule = addSpec(newState);
				if (itemWithRule.getRight() != null) {
					HassIoMetric metric = convertToMetrics(itemWithRule);
					LOGGER.info(".updateState got new state metric {}", metric);
				}
			}

		});
	}

	private Point convertToInfluxEvent(HassIoMetric metric) {
		return convertToInfluxEvent(metric, metric.getLastUpdated().getTime());
	}

	private Point convertToInfluxEvent(HassIoMetric metric, long timestampMilis) {
		Point point = Point.measurement("hassio")
				.time(timestampMilis, TimeUnit.MILLISECONDS)
				.tag(metric.getTags())
				.tag("friendly_name", metric.getFriendlyName())
				.tag("entity_id", metric.getEntityId())
				.tag("domain", metric.getDomain())
				.fields(Collections.unmodifiableMap(metric.getMeasurements()))
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
				result.addMeasurement(attributeName, attributeValue.toString());
			}
		}

		for (String attributeName : spec.getTagAttributes()) {
			Object attributeValue = state.getAttribute(attributeName);
			if (attributeValue != null) {
				result.addTag(attributeName, attributeValue.toString());
			}
		}

		String stateName = "state";
		if (!StringUtils.isBlank(spec.getOverwriteStateFieldWithAttribute())) {
			stateName = state.getAttribute(spec.getOverwriteStateFieldWithAttribute()).toString();
		}

		result.addMeasurement(stateName, state.getState());

		return result;
	}

}
