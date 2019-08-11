package org.insanedevelopment.hass.influx.gatherer.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpec;
import org.insanedevelopment.hass.influx.gatherer.model.FilterPathSpecComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

@Repository
public class ConfigRepositoryImpl {

	private static final FilterPathSpecComparator COMPARATOR = new FilterPathSpecComparator();

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRepositoryImpl.class);

	private String configFileName = "./config.yaml";

	private List<FilterPathSpec> configItems;

	private Yaml yaml;

	public ConfigRepositoryImpl() {
		Representer representer = new Representer() {

			@Override
			protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
				if (propertyValue == null) {
					return null;
				}
				return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
			}

		};
		representer.addClassTag(ConfigFileSaveObject.class, Tag.MAP);
		yaml = new Yaml(representer);
		configItems = readFile();
		configItems.sort(COMPARATOR);
	}

	private List<FilterPathSpec> readFile() {
		List<FilterPathSpec> result;
		try (InputStream inputStream = IOUtils.buffer(FileUtils.openInputStream(new File(configFileName)))) {
			result = yaml.loadAs(inputStream, ConfigFileSaveObject.class).getConfigurationLines();
		} catch (FileNotFoundException fe) {
			LOGGER.debug(".readFile - No config file found, returning empty data");
			result = new ArrayList<>();
		} catch (IOException e) {
			LOGGER.error(".readFile - Error occured while reading file", e);
			throw new RuntimeException(e);
		}
		return result;
	}

	public List<FilterPathSpec> getAllConfigElements() {
		return configItems;
	}

	public void deleteItem(int listPosition) {
		configItems.remove(listPosition);
		sortAndSaveFile();
	}

	public void addItem(FilterPathSpec item) {
		Validate.notNull(item);
		configItems.add(item);
		sortAndSaveFile();
	}

	public void replaceItem(int listPosition, FilterPathSpec item) {
		deleteItem(listPosition);
		addItem(item);
	}

	private void sortAndSaveFile() {
		configItems.sort(COMPARATOR);
		File configFile = new File(configFileName);
		try {
			FileUtils.forceMkdirParent(configFile);
			OutputStreamWriter os = new OutputStreamWriter(IOUtils.buffer(FileUtils.openOutputStream(configFile)));
			ConfigFileSaveObject saveMe = new ConfigFileSaveObject(configItems);
			yaml.dump(saveMe, os);
			os.close();
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}

	}

}
