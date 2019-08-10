package org.insanedevelopment.hass.influx.gatherer.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HassIoStateChangeData {

	@JsonProperty("entity_id")
	private String entityId;
	@JsonProperty("old_state")
	private HassIoState oldState;
	@JsonProperty("new_state")
	private HassIoState newState;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HassIoStateChangeData [entityId=");
		builder.append(entityId);
		builder.append(", oldState=");
		builder.append(oldState);
		builder.append(", newState=");
		builder.append(newState);
		builder.append("]");
		return builder.toString();
	}

	public String getEntityId() {
		return entityId;
	}

	public HassIoState getOldState() {
		return oldState;
	}

	public HassIoState getNewState() {
		return newState;
	}

}