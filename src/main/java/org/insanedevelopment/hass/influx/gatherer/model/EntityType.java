package org.insanedevelopment.hass.influx.gatherer.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public enum EntityType {

	SENSOR("sensor"),
	BINARY_SENSOR("binary_sensor"),
	CLIMATE("climate"),
	SWITCH("switch"),
	DIMMER("light"),
	COVER("cover"),
	SUN("sun"),
	SCENE("scene"),
	GROUP("group"),
	AUTOMATION("automation"),
	SCRIPT("script"),
	INPUT_SELECT("input_select"),
	INPUT_NUMBER("input_number"),
	MEDIA_PLAYER("media_player"),
	UNKNOWN(null);

	private final String hassioString;

	private EntityType(String hassioString) {
		this.hassioString = hassioString;
	}

	public static EntityType fromEntityId(String entityId) {
		Validate.isTrue(StringUtils.contains(entityId, "."), "EntityId must contain a dot (.)");
		String entityTypeFromId = StringUtils.substringBefore(entityId, ".");
		Validate.notBlank(entityTypeFromId, "Entity type from entityId must not be empty");

		for (EntityType entityType : EntityType.values()) {
			if (entityType.matchesHassIoString(entityTypeFromId)) {
				return entityType;
			}
		}

		return UNKNOWN;
		// throw new IllegalArgumentException("unknown entity type " +
		// entityTypeFromId + " entityId was " + entityId);
	}

	private boolean matchesHassIoString(String entityTypeFromId) {
		return hassioString == null ? false : hassioString.equals(entityTypeFromId);
	}

}
