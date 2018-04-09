package com.huya.search.restful;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.settings.ImmutableSettings;
import com.huya.search.util.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton
public class NettySettings extends ImmutableSettings {

    private static final Map<String, Object> initSettings;

    static {
        Properties properties = PropertiesUtils.getProperties("netty.properties");
        initSettings = new HashMap<>();
        assert properties != null;
        for (String key : properties.stringPropertyNames()) {
            initSettings.put(key, properties.getProperty(key));
        }
    }

    @Inject
    NettySettings() {
        super(initSettings, NettySettings.class.getClassLoader());
    }
}
