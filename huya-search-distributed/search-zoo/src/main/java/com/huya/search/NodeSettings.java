package com.huya.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.settings.ImmutableSettings;
import com.huya.search.util.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by zhangyiqun1@yy.com on 2017/10/17.
 */

@Singleton
public class NodeSettings extends ImmutableSettings {

    private static final Map<String, Object> nodeSettings;

    static {
        Properties properties = PropertiesUtils.getProperties("node.properties");
        nodeSettings = new HashMap<>();
        assert properties != null;
        for (String key : properties.stringPropertyNames()) {
            nodeSettings.put(key, properties.getProperty(key));
        }
    }

    @Inject
    public NodeSettings() {
        super(nodeSettings, NodeSettings.class.getClassLoader());
    }
}
