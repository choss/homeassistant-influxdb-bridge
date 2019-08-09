package org.insanedevelopment.hass.influx.gatherer.model.json;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HassIoState {

	@JsonProperty("attributes")
	private Map<String, Object> attributes;

	@JsonProperty("entity_id")
	private String entityId;

	@JsonProperty("state")
	private String state;

	@JsonProperty("last_changed")
	private Date lastChanged;

	@JsonProperty("last_updated")
	private Date lastUpdated;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HassIoState [attributes=");
		builder.append(attributes);
		builder.append(", entityId=");
		builder.append(entityId);
		builder.append(", state=");
		builder.append(state);
		builder.append(", lastChanged=");
		builder.append(lastChanged);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append("]");
		return builder.toString();
	}

}
