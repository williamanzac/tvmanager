package com.wing.configuration.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.wing.database.model.Configuration;

public class ConfigurationService {

	private static final String TORRENT_DESTINATION = "torrent.destination";
	private static final File file = new File("tvmanager.conf");
	private Configuration configuration;

	public Configuration loadConfiguration() throws IOException {
		if (configuration == null) {
			configuration = new Configuration();
			final Properties properties = new Properties();
			properties.load(new FileReader(file));

			final String value = properties.getProperty(TORRENT_DESTINATION);
			configuration.torrentDestination = new File(value);
		}
		return configuration;
	}

	public void saveConfiguration() throws IOException {
		final Properties properties = new Properties();

		properties.setProperty(TORRENT_DESTINATION,
				configuration.torrentDestination.getAbsolutePath());

		properties.store(new FileWriter(file), "");
	}

	String fieldNameToString(final Field field) {
		final String name = field.getName();
		final String[] camelCase = StringUtils
				.splitByCharacterTypeCamelCase(name);
		final String join = StringUtils.join(camelCase, '.');
		return join.toLowerCase();
	}

	String getPropertyForField(final Field field, final Properties properties) {
		final String key = fieldNameToString(field);
		return properties.getProperty(key);
	}

	<T> T getPropertyForField(final Field field, final Properties properties,
			final Class<T> clazz) {
		final String key = fieldNameToString(field);
		final String strVal = properties.getProperty(key);
		return null;
	}

	void configurationFromProperties(final Properties properties,
			final Configuration configuration) throws IllegalArgumentException,
			IllegalAccessException {
		final Field[] fields = configuration.getClass().getFields();
		if (fields == null) {
			return;
		}
		for (final Field field : fields) {
			final String value = getPropertyForField(field, properties);
			field.set(configuration, value);
		}
	}
}
