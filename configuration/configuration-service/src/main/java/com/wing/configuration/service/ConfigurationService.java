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

	public static final String DEFAULT_USERNAME = "admin";
	public static final String DEFAULT_PASSWORD = "admin";

	private static final String TORRENT_DESTINATION = "torrent.destination";
	private static final String TORRENT_USERNAME = "torrent.username";
	private static final String TORRENT_PASSWORD = "torrent.password";
	private static final String SHOW_DESTINATION = "show.destination";
	private static final File file = new File("tvmanager.conf");
	private Configuration configuration;

	public Configuration loadConfiguration() throws IOException {
		if (configuration == null) {
			configuration = new Configuration();
			if (file.exists()) {
				final Properties properties = new Properties();
				properties.load(new FileReader(file));

				String value = properties.getProperty(TORRENT_DESTINATION);
				if (value != null) {
					configuration.torrentDestination = new File(value);
				}

				value = properties.getProperty(TORRENT_USERNAME, DEFAULT_USERNAME);
				configuration.torrentUsername = value;

				value = properties.getProperty(TORRENT_PASSWORD, DEFAULT_PASSWORD);
				configuration.torrentPassword = value;

				value = properties.getProperty(SHOW_DESTINATION);
				if (value != null) {
					configuration.showDestination = new File(value);
				}
			} else {
				configuration.torrentPassword = DEFAULT_PASSWORD;
				configuration.torrentUsername = DEFAULT_USERNAME;
			}
		}
		return configuration;
	}

	public void saveConfiguration(Configuration configuration) throws IOException {
		final Properties properties = new Properties();

		properties.setProperty(TORRENT_DESTINATION, configuration.torrentDestination.getAbsolutePath());
		properties.setProperty(TORRENT_USERNAME, configuration.torrentUsername);
		properties.setProperty(TORRENT_PASSWORD, configuration.torrentPassword);
		properties.setProperty(SHOW_DESTINATION, configuration.showDestination.getAbsolutePath());

		properties.store(new FileWriter(file), "");
	}

	String fieldNameToString(final Field field) {
		final String name = field.getName();
		final String[] camelCase = StringUtils.splitByCharacterTypeCamelCase(name);
		final String join = StringUtils.join(camelCase, '.');
		return join.toLowerCase();
	}

	String getPropertyForField(final Field field, final Properties properties) {
		final String key = fieldNameToString(field);
		return properties.getProperty(key);
	}

	<T> T getPropertyForField(final Field field, final Properties properties, final Class<T> clazz) {
		final String key = fieldNameToString(field);
		final String strVal = properties.getProperty(key);
		return null;
	}

	void configurationFromProperties(final Properties properties, final Configuration configuration)
			throws IllegalArgumentException, IllegalAccessException {
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
