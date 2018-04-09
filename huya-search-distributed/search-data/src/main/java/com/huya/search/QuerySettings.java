package com.huya.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.settings.ImmutableSettings;
import com.huya.search.util.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/6.
 */
@Singleton
public class QuerySettings extends ImmutableSettings {

    private static final Map<String, Object> initSettings;

    static {
        Properties properties = PropertiesUtils.getProperties("query.properties");
        initSettings = new HashMap<>();
        assert properties != null;
        for (String key : properties.stringPropertyNames()) {
            initSettings.put(key, properties.getProperty(key));
        }
    }

    @Inject
    QuerySettings() {
        super(initSettings, QuerySettings.class.getClassLoader());
    }
}
