package org.insanedevelopment.hass.influx.gatherer.model.json;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HassIoStateChangeEvent {

	@JsonProperty("event_type")
	private String eventType;
	@JsonProperty("data")
	private HassIoStateChangeData data;
	@JsonProperty("origin")
	private String origin;
	@JsonProperty("time_fired")
	private Date timeFired;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HassIoStateChangeEvent [eventType=");
		builder.append(eventType);
		builder.append(", data=");
		builder.append(data);
		builder.append(", origin=");
		builder.append(origin);
		builder.append(", timeFired=");
		builder.append(timeFired);
		builder.append("]");
		return builder.toString();
	}

	public String getEventType() {
		return eventType;
	}

	public HassIoStateChangeData getData() {
		return data;
	}

	public String getOrigin() {
		return origin;
	}

	public Date getTimeFired() {
		return timeFired;
	}

}