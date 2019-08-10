package org.insanedevelopment.hass.influx.gatherer.service;

import javax.annotation.PostConstruct;

import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpec;
import org.insanedevelopment.hass.influx.gatherer.repository.HassioRestStateClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfluxDbSendingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbSendingService.class);

	@Autowired
	private HassioRestStateClientImpl restClient;

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

		FilterPathSpec item = new FilterPathSpec("sun.sun");

		restClient.getAllCurrentStates()
				.stream()
				.filter(ha -> item.matches(ha.getEntityId()))
				.forEach(ha -> LOGGER.info("Matched {}", ha));
	}

}
