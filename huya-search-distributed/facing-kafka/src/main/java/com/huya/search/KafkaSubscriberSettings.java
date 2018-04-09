package com.huya.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.settings.ImmutableSettings;
import com.huya.search.util.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/30.
 */
@Singleton
public class KafkaSubscriberSettings extends ImmutableSettings {

    private static final Map<String, Object> kafkaSettings;

    static {
        Properties properties = PropertiesUtils.getProperties("kafka-subscriber.properties");
        kafkaSettings = new HashMap<>();
        assert properties != null;
        for (String key : properties.stringPropertyNames()) {
            kafkaSettings.put(key, properties.getProperty(key));
        }
    }


    @Inject
    KafkaSubscriberSettings() {
        super(kafkaSettings, KafkaSubscriberSettings.class.getClassLoader());
    }

}
