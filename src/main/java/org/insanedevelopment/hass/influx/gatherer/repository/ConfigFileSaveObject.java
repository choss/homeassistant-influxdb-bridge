package org.insanedevelopment.hass.influx.gatherer.repository;

import java.util.List;

import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpec;

public class ConfigFileSaveObject {

	private List<FilterPathSpec> configurationLines;

	public ConfigFileSaveObject() {
		// there for snakeYaml
	}

	public ConfigFileSaveObject(List<FilterPathSpec> configItems) {
		configurationLines = configItems;
	}

	public List<FilterPathSpec> getConfigurationLines() {
		return configurationLines;
	}

	public void setConfigurationLines(List<FilterPathSpec> configurationLines) {
		this.configurationLines = configurationLines;
	}

}
