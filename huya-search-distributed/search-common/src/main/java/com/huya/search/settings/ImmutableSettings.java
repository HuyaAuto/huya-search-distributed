/*
 * Licensed under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.huya.search.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huya.search.io.SearchInputStream;
import com.huya.search.io.SearchOutputStream;
import com.huya.search.settings.loader.SettingsLoader;
import com.huya.search.settings.loader.SettingsLoaderFactory;
import com.huya.search.util.Booleans;
import com.huya.search.util.PropertyPlaceholder;
import com.huya.search.util.Streams;
import com.huya.search.util.Strings;

/**
 * An immutable implementation of {@link Settings}.
 */
public class ImmutableSettings implements Settings {

	static {
		streamReaders.put(Settings.class, ImmutableSettings::readStaticFrom);
		streamReaders.put(ImmutableSettings.class, ImmutableSettings::readStaticFrom);
	}

	public static final Settings EMPTY = new Builder().build();
	public static final String FLAT_SETTINGS_KEY = "flat_settings";
	private static final Pattern ARRAY_PATTERN = Pattern.compile("(.*)\\.\\d+$");

	private ImmutableMap<String, Object> settings;
	private transient ClassLoader classLoader;

	protected ImmutableSettings(Map<String, Object> settings, ClassLoader classLoader) {
		this.settings = ImmutableMap.copyOf(settings);
		this.classLoader = classLoader;
	}

	@Override
	public ClassLoader getClassLoaderIfSet() {
		return this.classLoader;
	}

	@Override
	public ImmutableMap<String, Object> asMap() {
		return settings;
	}

	@Override
	public Properties asProperties() {
		ImmutableMap<String, Object> map =  asMap();
		Properties properties = new Properties();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			properties.put(entry.getKey(), entry.getValue());
		}
		return properties;
	}

	@Override
	public Properties asProperties(String startWith, boolean include) {
		ImmutableMap<String, Object> map =  asMap();
		Properties properties = new Properties();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			boolean startWithFlag = key.startsWith(startWith);
			if ((include && startWithFlag) || (!include && !startWithFlag)) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}
		return properties;
	}

	@Override
	public Settings getAsSettings(String setting) {
		Builder builder = new Builder();
		if (null != settings.get(setting)) {
			builder.put(settings.get(setting));
		}

		final String prefix = setting + ".";
		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			if(entry.getKey().startsWith(prefix)) {
				builder.put(entry.getKey().substring(prefix.length()), entry.getValue());
			}
		}
		builder.classLoader(classLoader);
		return builder.build();
	}

	public List<String> getPrefix(String setting) {
		List<String> temp = new ArrayList<>();
		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			if (entry.getKey().startsWith(setting)) {
				temp.add((String)entry.getValue());
			}
		}
		return temp;
	}

	@Override
	public String get(String setting) {
		Object retVal = settings.get(setting);
		if (null == retVal) {
			String[] keys = Strings.splitStringToArray(setting, '.', true);
			Map map = settings;
			for (int i = 0; i < keys.length - 1; ++i) {
				Object obj = map.get(keys[i]);
				if (null == obj || !(obj instanceof Map)) {
					map = null;
					break;
				}
				map = (Map) obj;
			}
			if (null != map)
				retVal = map.get(keys[keys.length - 1]);
		}
		return retVal == null ? null : retVal.toString();
	}

	@Override
	public String getRequired(String setting) {
		String val = get(setting);
		if (null == val)
			throw new SettingRequiredException(setting);
		return val;
	}

	@Override
	public String get(String[] settings) {
		for (String setting : settings) {
			String retVal = get(setting);
			if (retVal != null) {
				return retVal;
			}
		}
		return null;
	}

	@Override
	public String get(String setting, String defaultValue) {
		String retVal = get(setting);
		return retVal == null ? defaultValue : retVal;
	}

	@Override
	public String get(String[] settings, String defaultValue) {
		String retVal = get(settings);
		return retVal == null ? defaultValue : retVal;
	}

	@Override
	public Float getAsFloat(String setting, Float defaultValue) {
		String sValue = get(setting);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse float setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Float getRequiredAsFloat(String setting) throws SettingsException {
		String sValue = getRequired(setting);
		try {
			return Float.parseFloat(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse float setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Float getAsFloat(String[] settings, Float defaultValue) throws SettingsException {
		String sValue = get(settings);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse float setting [" + Arrays.toString(settings) + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Double getAsDouble(String setting, Double defaultValue) {
		String sValue = get(setting);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse double setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Double getRequiredAsDouble(String setting) throws SettingsException {
		String sValue = getRequired(setting);
		try {
			return Double.parseDouble(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse double setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Double getAsDouble(String[] settings, Double defaultValue) {
		String sValue = get(settings);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse double setting [" + Arrays.toString(settings) + "] with value [" + sValue + "]", e);
		}
	}


	@Override
	public Integer getAsInt(String setting, Integer defaultValue) {
		String sValue = get(setting);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse int setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Integer getRequiredAsInt(String setting) throws SettingsException {
		String sValue = getRequired(setting);
		try {
			return Integer.parseInt(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse int setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Integer getAsInt(String[] settings, Integer defaultValue) {
		String sValue = get(settings);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse int setting [" + Arrays.toString(settings) + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Long getAsLong(String setting, Long defaultValue) {
		String sValue = get(setting);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse long setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Long getRequiredAsLong(String setting) throws SettingsException {
		String sValue = getRequired(setting);
		try {
			return Long.parseLong(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse long setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Long getAsLong(String[] settings, Long defaultValue) {
		String sValue = get(settings);
		if (sValue == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(sValue);
		} catch (NumberFormatException e) {
			throw new SettingsException("Failed to parse long setting [" + Arrays.toString(settings) + "] with value [" + sValue + "]", e);
		}
	}

	@Override
	public Boolean getAsBoolean(String setting, Boolean defaultValue) {
		return Booleans.parseBoolean(get(setting), defaultValue);
	}

	@Override
	public Boolean getRequiredAsBoolean(String setting) throws SettingsException {
		return Booleans.parseBoolean(getRequired(setting), false);
	}

	@Override
	public Boolean getAsBoolean(String[] settings, Boolean defaultValue) {
		return Booleans.parseBoolean(get(settings), defaultValue);
	}


	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ImmutableSettings.class.getClassLoader();
		}
		return cl;
	}

	public ClassLoader getClassLoader() {
		return this.classLoader == null ? getDefaultClassLoader() : classLoader;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public <T> Class<? extends T> getAsClass(String setting, Class<? extends T> defaultClazz) throws NoClassSettingsException {
		String sValue = get(setting);
		if (sValue == null) {
			return defaultClazz;
		}
		try {
			return (Class<? extends T>) getClassLoader().loadClass(sValue);
		} catch (ClassNotFoundException e) {
			throw new NoClassSettingsException("Failed to load class setting [" + setting + "] with value [" + sValue + "]", e);
		}
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public <T> Class<? extends T> getAsClass(String setting, Class<? extends T> defaultClazz, String prefixPackage, String suffixClassName) throws NoClassSettingsException {
		String sValue = get(setting);
		if (sValue == null) {
			return defaultClazz;
		}
		String fullClassName = sValue;
		try {
			return (Class<? extends T>) getClassLoader().loadClass(fullClassName);
		} catch (ClassNotFoundException e) {
			String prefixValue = prefixPackage;
			int packageSeparator = sValue.lastIndexOf('.');
			if (packageSeparator > 0) {
				prefixValue = sValue.substring(0, packageSeparator + 1);
				sValue = sValue.substring(packageSeparator + 1);
			}
			fullClassName = prefixValue + Strings.capitalize(Strings.toCamelCase(sValue)) + suffixClassName;
			try {
				return (Class<? extends T>) getClassLoader().loadClass(fullClassName);
			} catch (ClassNotFoundException | NoClassDefFoundError e1) {
				return loadClass(prefixValue, sValue, suffixClassName, setting);
			}
		}
	}

	private <T> Class<? extends T> loadClass(String prefixValue, String sValue, String suffixClassName, String setting) {
		String fullClassName = prefixValue + Strings.toCamelCase(sValue).toLowerCase(Locale.ROOT) + "." + Strings.capitalize(Strings.toCamelCase(sValue)) + suffixClassName;
		try {
			return (Class<? extends T>) getClassLoader().loadClass(fullClassName);
		} catch (ClassNotFoundException e2) {
			throw new NoClassSettingsException("Failed to load class setting [" + setting + "] with value [" + get(setting) + "]", e2);
		}
	}

	@Override
	public String[] getAsArray(String settingPrefix) throws SettingsException {
		return getAsArray(settingPrefix, Strings.EMPTY_ARRAY, true);
	}

	@Override
	public TimeValue getAsTime(String setting, TimeValue defaultValue) throws SettingsException {
		return TimeValue.parseTimeValue(get(setting), defaultValue);
	}

	@Override
	public TimeValue getRequiredAsTime(String setting) throws SettingsException {
		return TimeValue.parseTimeValue(getRequired(setting), null);
	}

	@Override
	public String[] getAsArray(String settingPrefix, String[] defaultArray) throws SettingsException {
		return getAsArray(settingPrefix, defaultArray, true);
	}

	@Override
	public String[] getAsArray(String settingPrefix, String[] defaultArray, Boolean commaDelimited) throws SettingsException {
		List<String> result = Lists.newArrayList();

		if (get(settingPrefix) != null) {
			if (commaDelimited) {
				String[] strings = Strings.splitStringByCommaToArray(get(settingPrefix), true);
				if (strings.length > 0) {
					for (String string : strings) {
						result.add(string.trim());
					}
				}
			} else {
				result.add(get(settingPrefix).trim());
			}
		}

		int counter = 0;
		while (true) {
			String value = get(settingPrefix + '.' + (counter++));
			if (value == null) {
				break;
			}
			result.add(value.trim());
		}
		if (result.isEmpty()) {
			return defaultArray;
		}
		return result.toArray(new String[result.size()]);
	}

	@Override
	public Map<String, Settings> getGroups(String key, String groupKey) {
		if (null == settings.get(key))
			return ImmutableMap.of();
		if (settings.get(key) instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) settings.get(key);
			if (null == map.get(groupKey))
				return ImmutableMap.of();
			else
				return ImmutableMap.of(groupKey, builder().put(map).build());
		} else if (settings.get(key) instanceof List) {
			Map<String, Settings> retMap = Maps.newHashMap();
			for (Object element : (List) settings.get(key)) {
				Settings settings = builder().put(element).build();
				if (null != settings.get(groupKey))
					retMap.put(settings.get(groupKey), settings);
			}
			return retMap;
		}
		return ImmutableMap.of();
	}

	@Override
	public List<Settings> getAsSettingsList(String setting) {
		Object sub = settings.get(setting);
		if (null == sub)
			return null;
		if (sub instanceof List) {
			List<Settings> subs = new LinkedList<>();
			for (Object obj : (Iterable) sub) {
				subs.add(builder().put(obj).build());
			}
			return subs;
		} else {
			return ImmutableList.of(builder().put(sub).build());
		}
	}

	@Override
	public Set<String> names() {
		Set<String> names = new HashSet<>();
		for (String key : settings.keySet()) {
			int i = key.indexOf(".");
			if (i < 0) {
				names.add(key);
			} else {
				names.add(key.substring(0, i));
			}
		}
		return names;
	}

	@Override
	public String toDelimitedString(char delimiter) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append(delimiter);
		}
		return sb.toString();
	}

	@Override
	public boolean isEmpty() {
		return settings.isEmpty();
	}

	@Override
	public boolean has(String setting) {
		return settings.containsKey(setting);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ImmutableSettings that = (ImmutableSettings) o;

		if (classLoader != null ? !classLoader.equals(that.classLoader) : that.classLoader != null) return false;
		if (settings != null ? !settings.equals(that.settings) : that.settings != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = settings != null ? settings.hashCode() : 0;
		result = 31 * result + (classLoader != null ? classLoader.hashCode() : 0);
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a builder to be used in order to build settings.
	 */
	public static Builder settingsBuilder() {
		return new Builder();
	}

	@Override
	public void writeTo(SearchOutputStream out) throws IOException {
		out.writeMap(settings);
	}

	/**
	 * A builder allowing to put different settings and then {@link #build()} an immutable
	 * settings implementation. Use {@link ImmutableSettings#settingsBuilder()} in order to
	 * construct it.
	 */
	public static class Builder implements Settings.Builder {

		public static final Settings EMPTY_SETTINGS = new Builder().build();

		private final Map<String, Object> map = new LinkedHashMap<>();

		private ClassLoader classLoader;

		private Builder() {

		}

		public Map<String, Object> internalMap() {
			return this.map;
		}

		/**
		 * Removes the provided setting from the internal map holding the current list of settings.
		 */
		public Object remove(String key) {
			return map.remove(key);
		}

		/**
		 * Returns a setting value based on the setting key.
		 */
		public Object get(String key) {
			Object retVal = map.get(key);
			if (retVal != null) {
				return retVal;
			}
			// try camel case version
			return map.get(Strings.toCamelCase(key));
		}

		public Builder put(List<Object> settings) {
			for (Object obj : settings) {
				put(obj);
			}
			return this;
		}

		/**
		 * Puts tuples of key value pairs of settings. Simplified version instead of repeating calling
		 * put for each one.
		 */
		public Builder put(Object... settings) {
			if (settings.length == 1) {
				// support cases where the actual type gets lost down the road...
				if (settings[0] instanceof Map) {
					//noinspection unchecked
					return put((Map) settings[0]);
				} else if (settings[0] instanceof Settings) {
					return put((Settings) settings[0]);
				} else if (settings[0] instanceof List) {
					return put((List) settings[0]);
				}
			}
			if ((settings.length % 2) != 0) {
//				throw new IllegalArgumentException("array settings of key + value order doesn't hold correct number of arguments (" + settings.length + ")");
				return this;
			}
			for (int i = 0; i < settings.length; i++) {
				put(settings[i++].toString(), settings[i].toString());
			}
			return this;
		}

		/**
		 * Sets a setting with the provided setting key and value.
		 *
		 * @param key   The setting key
		 * @param value The setting value
		 * @return The builder
		 */
		public Builder put(String key, String value) {
			map.put(key, value);
			return this;
		}

		/**
		 * Sets a setting with the provided setting key and class as value.
		 *
		 * @param key   The setting key
		 * @param clazz The setting class value
		 * @return The builder
		 */
		public Builder put(String key, Class clazz) {
			map.put(key, clazz.getName());
			return this;
		}

		/**
		 * Sets the setting with the provided setting key and the boolean value.
		 *
		 * @param setting The setting key
		 * @param value   The boolean value
		 * @return The builder
		 */
		public Builder put(String setting, boolean value) {
			put(setting, String.valueOf(value));
			return this;
		}

		/**
		 * Sets the setting with the provided setting key and the int value.
		 *
		 * @param setting The setting key
		 * @param value   The int value
		 * @return The builder
		 */
		public Builder put(String setting, int value) {
			put(setting, String.valueOf(value));
			return this;
		}


		/**
		 * Sets the setting with the provided setting key and the long value.
		 *
		 * @param setting The setting key
		 * @param value   The long value
		 * @return The builder
		 */
		public Builder put(String setting, long value) {
			put(setting, String.valueOf(value));
			return this;
		}

		/**
		 * Sets the setting with the provided setting key and the float value.
		 *
		 * @param setting The setting key
		 * @param value   The float value
		 * @return The builder
		 */
		public Builder put(String setting, float value) {
			put(setting, String.valueOf(value));
			return this;
		}

		/**
		 * Sets the setting with the provided setting key and the double value.
		 *
		 * @param setting The setting key
		 * @param value   The double value
		 * @return The builder
		 */
		public Builder put(String setting, double value) {
			put(setting, String.valueOf(value));
			return this;
		}

		/**
		 * Sets the setting with the provided setting key and the time value.
		 *
		 * @param setting The setting key
		 * @param value   The time value
		 * @return The builder
		 */
		public Builder put(String setting, long value, TimeUnit timeUnit) {
			put(setting, timeUnit.toMillis(value));
			return this;
		}

		/**
		 * Sets the setting with the provided setting key and an array of values.
		 *
		 * @param setting The setting key
		 * @param values  The values
		 * @return The builder
		 */
		public Builder putArray(String setting, String... values) {
			remove(setting);
			int counter = 0;
			while (true) {
				Object value = map.remove(setting + '.' + (counter++));
				if (value == null) {
					break;
				}
			}
			for (int i = 0; i < values.length; i++) {
				put(setting + "." + i, values[i]);
			}
			return this;
		}

		/**
		 * Sets the setting group.
		 */
		public Builder put(String settingPrefix, String groupName, String[] settings, String[] values) throws SettingsException {
			if (settings.length != values.length) {
				throw new SettingsException("The settings length must match the value length");
			}
			for (int i = 0; i < settings.length; i++) {
				if (values[i] == null) {
					continue;
				}
				put(settingPrefix + "." + groupName + "." + settings[i], values[i]);
			}
			return this;
		}

		/**
		 * Sets all the provided settings.
		 */
		public Builder put(Settings settings) {
			removeNonArraysFieldsIfNewSettingsContainsFieldAsArray(settings.asMap());
			map.putAll(settings.asMap());
			classLoader = settings.getClassLoaderIfSet();
			return this;
		}

		/**
		 * Sets all the provided settings.
		 */
		public Builder put(Map<String, Object> settings) {
			removeNonArraysFieldsIfNewSettingsContainsFieldAsArray(settings);
			map.putAll(settings);
			return this;
		}

		/**
		 * Removes non array values from the existing map, if settings contains an array value instead
		 * <p>
		 * Example:
		 * Existing map contains: {key:value}
		 * New map contains: {key:[value1,value2]} (which has been flattened to {}key.0:value1,key.1:value2})
		 * <p>
		 * This ensure that that the 'key' field gets removed from the map in order to override all the
		 * data instead of merging
		 */
		private void removeNonArraysFieldsIfNewSettingsContainsFieldAsArray(Map<String, Object> settings) {
			List<String> prefixesToRemove = new ArrayList<>();
			for (final Map.Entry<String, Object> entry : settings.entrySet()) {
				final Matcher matcher = ARRAY_PATTERN.matcher(entry.getKey());
				if (matcher.matches()) {
					prefixesToRemove.add(matcher.group(1));
				} else if (Iterables.any(map.keySet(), startsWith(entry.getKey() + "."))) {
					prefixesToRemove.add(entry.getKey());
				}
			}
			for (String prefix : prefixesToRemove) {
				Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Object> entry = iterator.next();
					if (entry.getKey().startsWith(prefix + ".") || entry.getKey().equals(prefix)) {
						iterator.remove();
					}
				}
			}
		}

		/**
		 * Sets all the provided settings.
		 */
		public Builder put(Properties properties) {
			for (Map.Entry entry : properties.entrySet()) {
				map.put((String) entry.getKey(), (String) entry.getValue());
			}
			return this;
		}

		public Builder loadFromDelimitedString(String value, char delimiter) {
			String[] values = Strings.splitStringToArray(value, delimiter, true);
			for (String s : values) {
				int index = s.indexOf('=');
				if (index == -1) {
					throw new IllegalArgumentException("value [" + s + "] for settings loaded with delimiter [" + delimiter + "] is malformed, missing =");
				}
				map.put(s.substring(0, index), s.substring(index + 1));
			}
			return this;
		}

		/**
		 * Loads settings from the actual string content that represents them using the
		 * {@link SettingsLoaderFactory#loaderFromSource(String)}.
		 */
		public Builder loadFromSource(String source) {
			SettingsLoader settingsLoader = SettingsLoaderFactory.loaderFromSource(source);
			try {
				Map<String, Object> loadedSettings = settingsLoader.load(source);
				put(loadedSettings);
			} catch (Exception e) {
				throw new SettingsException("Failed to load settings from [" + source + "]", e);
			}
			return this;
		}

		/**
		 * Loads settings from a url that represents them using the
		 * {@link SettingsLoaderFactory#loaderFromSource(String)}.
		 */
		public Builder loadFromUrl(URL url) throws SettingsException {
			try {
				return loadFromStream(url.toExternalForm(), url.openStream());
			} catch (IOException e) {
				throw new SettingsException("Failed to open stream for url [" + url.toExternalForm() + "]", e);
			}
		}

		public Builder loadFromPath(Path path) throws SettingsException {
			try {
				return loadFromUrl(path.toUri().toURL());
			} catch (MalformedURLException e) {
				throw new SettingsException("Failed get get url for path [" + path + "]", e);
			}
		}

		/**
		 * Loads settings from a stream that represents them using the
		 * {@link SettingsLoaderFactory#loaderFromSource(String)}.
		 */
		public Builder loadFromStream(String resourceName, InputStream is) throws SettingsException {
			SettingsLoader settingsLoader = SettingsLoaderFactory.loaderFromResource(resourceName);
			try {
				Map<String, Object> loadedSettings = settingsLoader.load(Streams.copyToString(new InputStreamReader(is, Charsets.UTF_8)));
				put(loadedSettings);
			} catch (Exception e) {
				throw new SettingsException("Failed to load settings from [" + resourceName + "]", e);
			}
			return this;
		}

		/**
		 * Loads settings from classpath that represents them using the
		 * {@link SettingsLoaderFactory#loaderFromSource(String)}.
		 */
		public Builder loadFromClasspath(String resourceName) throws SettingsException {
			ClassLoader classLoader = this.classLoader;
			if (classLoader == null) {
				classLoader = getDefaultClassLoader();
			}
			InputStream is = classLoader.getResourceAsStream(resourceName);
			if (is == null) {
				return this;
			}

			return loadFromStream(resourceName, is);
		}

		/**
		 * Sets the class loader associated with the settings built.
		 */
		public Builder classLoader(ClassLoader classLoader) {
			this.classLoader = classLoader;
			return this;
		}

		/**
		 * Puts all the properties with keys starting with the provided <tt>prefix</tt>.
		 *
		 * @param prefix     The prefix to filter property key by
		 * @param properties The properties to put
		 * @return The builder
		 */
		public Builder putProperties(String prefix, Properties properties) {
			for (Object key1 : properties.keySet()) {
				String key = (String) key1;
				String value = properties.getProperty(key);
				if (key.startsWith(prefix)) {
					map.put(key.substring(prefix.length()), value);
				}
			}
			return this;
		}

		/**
		 * Puts all the properties with keys starting with the provided <tt>prefix</tt>.
		 *
		 * @param prefix     The prefix to filter property key by
		 * @param properties The properties to put
		 * @return The builder
		 */
		public Builder putProperties(String prefix, Properties properties, String[] ignorePrefixes) {
			for (Object key1 : properties.keySet()) {
				String key = (String) key1;
				String value = properties.getProperty(key);
				if (key.startsWith(prefix)) {
					boolean ignore = false;
					for (String ignorePrefix : ignorePrefixes) {
						if (key.startsWith(ignorePrefix)) {
							ignore = true;
							break;
						}
					}
					if (!ignore) {
						map.put(key.substring(prefix.length()), value);
					}
				}
			}
			return this;
		}

		/**
		 * Runs across all the settings set on this builder and replaces <tt>${...}</tt> elements in the
		 * each setting value according to the following logic:
		 * <p>
		 * <p>First, tries to resolve it against a System property ({@link System#getProperty(String)}), next,
		 * tries and resolve it against an environment variable ({@link System#getenv(String)}), and last, tries
		 * and replace it with another setting already set on this builder.
		 */
		public Builder replacePropertyPlaceholders() {
			PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder("${", "}", false);
			PropertyPlaceholder.PlaceholderResolver placeholderResolver = new PropertyPlaceholder.PlaceholderResolver() {
				@Override
				public String resolvePlaceholder(String placeholderName) {
					if (placeholderName.startsWith("env.")) {
						// explicit env var prefix
						return System.getenv(placeholderName.substring("env.".length()));
					}
					String value = System.getProperty(placeholderName);
					if (value != null) {
						return value;
					}
					value = System.getenv(placeholderName);
					if (value != null) {
						return value;
					}
					return map.get(placeholderName).toString();
				}

				@Override
				public boolean shouldIgnoreMissing(String placeholderName) {
					// if its an explicit env var, we are ok with not having a value for it and treat it as optional
					if (placeholderName.startsWith("env.") || placeholderName.startsWith("prompt.")) {
						return true;
					}
					return false;
				}

				@Override
				public boolean shouldRemoveMissingPlaceholder(String placeholderName) {
					if (placeholderName.startsWith("prompt.")) {
						return false;
					}
					return true;
				}
			};
			for (Map.Entry<String, Object> entry : Maps.newHashMap(map).entrySet()) {
				String value = propertyPlaceholder.replacePlaceholders(entry.getValue().toString(), placeholderResolver);
				// if the values exists and has length, we should maintain it  in the map
				// otherwise, the replace process resolved into removing it
				if (Strings.hasLength(value)) {
					map.put(entry.getKey(), value);
				} else {
					map.remove(entry.getKey());
				}
			}
			return this;
		}

		/**
		 * Checks that all settings in the builder start with the specified prefix.
		 * <p>
		 * If a setting doesn't start with the prefix, the builder appends the prefix to such setting.
		 */
		public Builder normalizePrefix(String prefix) {
			Map<String, Object> replacements = Maps.newHashMap();
			Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				if (!entry.getKey().startsWith(prefix)) {
					replacements.put(prefix + entry.getKey(), entry.getValue());
					iterator.remove();
				}
			}
			map.putAll(replacements);
			return this;
		}

		/**
		 * Builds a {@link Settings} (underlying uses {@link ImmutableSettings}) based on everything
		 * set on this builder.
		 */
		public Settings build() {
			return new ImmutableSettings(Collections.unmodifiableMap(map), classLoader);
		}
	}

	private static StartsWithPredicate startsWith(String prefix) {
		return new StartsWithPredicate(prefix);
	}

	private static final class StartsWithPredicate implements Predicate<String> {

		private String prefix;

		public StartsWithPredicate(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public boolean apply(String input) {
			return input.startsWith(prefix);
		}
	}

	public static Settings readStaticFrom(SearchInputStream in) throws IOException {
		return ImmutableSettings.builder().put(in.readMap()).build();
	}
}
