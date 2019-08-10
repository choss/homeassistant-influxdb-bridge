package org.insanedevelopment.hass.influx.gatherer.model;

import java.math.BigDecimal;
import java.util.Map;

public class HassIoMetric {
	private Map<String, String> attributes;
	private Map<String, BigDecimal> measurements;

	private String entityId;
	private String domain;
	private String entityName;
	private String friendlyName;

}
