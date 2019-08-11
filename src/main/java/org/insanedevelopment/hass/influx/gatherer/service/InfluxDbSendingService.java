package org.insanedevelopment.hass.influx.gatherer.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpec;
import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoState;
import org.insanedevelopment.hass.influx.gatherer.repository.ConfigRepositoryImpl;
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

	/**
	 * Config of the application says which
	 * domains/states should be captured or not
	 * Same for attributes
	 * Config is on domain or entityId (with exclusions)
	 */
	@PostConstruct
	public void init() {
		// restClient.subscribeToChanges(new HassIoStateChangeObserver() {
		//
		// @Override
		// public void update(HassIoState oldState, HassIoState newState) {
		// LOGGER.info(".updateState got new state {}" + newState);
		// }
		//
		// });

	}

	public void getFilterAndLog() {
		Flux<HassIoState> repositoryResult = restClient.getAllCurrentStates();

		repositoryResult
				.filter(has -> shouldBeReported(has.getEntityId()))
				.doOnNext(ha -> LOGGER.info("Matched {}", ha))
				.subscribe();
	}

	private boolean shouldBeReported(String entityId) {
		List<FilterPathSpec> configElements = configRepository.getAllConfigElements();
		for (FilterPathSpec filterPathSpec : configElements) {
			if (filterPathSpec.matches(entityId)) {
				return true;
			}
		}
		return false;
	}

}
