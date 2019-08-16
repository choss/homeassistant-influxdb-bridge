package org.insanedevelopment.hass.influx.gatherer.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class HassIoMetric {
	private Map<String, String> tags = new HashMap<>();
	private Map<String, Object> measurements = new HashMap<>();

	private String entityId;
	private String domain;
	private String entityName;
	private String friendlyName;
	private Date lastUpdated;

	public HassIoMetric(String entityId, Date lastUpdated) {
		this.entityId = entityId;
		domain = StringUtils.substringBefore(entityId, ".");
		entityName = StringUtils.substringAfter(entityId, ".");
		this.lastUpdated = lastUpdated;
	}

	public void addMeasurement(String attributeName, Object attributeValue) {
		if (attributeValue != null) {
			measurements.put(attributeName, attributeValue);
		}
	}

	public void addTag(String attributeName, String attributeValue) {
		tags.put(attributeName, attributeValue);
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public Map<String, Object> getMeasurements() {
		return measurements;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getDomain() {
		return domain;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HassIoMetric [entityName=");
		builder.append(entityName);
		builder.append(", entityId=");
		builder.append(entityId);
		builder.append(", friendlyName=");
		builder.append(friendlyName);
		builder.append(", tags=");
		builder.append(tags);
		builder.append(", measurements=");
		builder.append(measurements);
		builder.append(", domain=");
		builder.append(domain);
		builder.append("]");
		return builder.toString();
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public boolean isValid() {
		return measurements.size() > 0;
	}

}
