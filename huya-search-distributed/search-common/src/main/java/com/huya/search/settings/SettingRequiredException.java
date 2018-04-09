package com.huya.search.settings;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class SettingRequiredException extends SettingsException {

	public SettingRequiredException(String settingKey) {
		super("setting \""+settingKey+"\" is required");
	}
}
