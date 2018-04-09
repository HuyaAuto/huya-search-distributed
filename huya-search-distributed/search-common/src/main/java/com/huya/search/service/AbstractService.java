package com.huya.search.service;

import com.huya.search.settings.ImmutableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huya.search.settings.Settings;

/**
 * @author colin.ke keqinwu@yy.com
 */
public abstract class AbstractService implements Service {

	protected final Logger logger;

	protected Settings settings;

	public AbstractService(Settings settings) {
		logger = LoggerFactory.getLogger(getClass());
		this.settings = settings;
	}

	public AbstractService() {
		logger = LoggerFactory.getLogger(getClass());
		this.settings = ImmutableSettings.EMPTY;
	}

}
