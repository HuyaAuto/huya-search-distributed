package com.huya.search.settings.loader;

import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class YamlSettingsLoader implements SettingsLoader {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> load(String source) throws IOException {
		return (Map<String, Object>) new Yaml().load(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> load(byte[] source) throws IOException {
		return (Map<String, Object>) new Yaml().load(new InputStreamReader(new ByteArrayInputStream(source)));
	}
}
