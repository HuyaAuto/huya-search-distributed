package com.huya.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.settings.ImmutableSettings;
import com.huya.search.util.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton
public class KafkaProducerSettings extends ImmutableSettings {

    private static final Map<String, Object> kafkaSettings;

    static {
        Properties properties = PropertiesUtils.getProperties("kafka-producer.properties");
        kafkaSettings = new HashMap<>();
        assert properties != null;
        for (String key : properties.stringPropertyNames()) {
            kafkaSettings.put(key, properties.getProperty(key));
        }
    }

    @Inject
    KafkaProducerSettings() {
        super(kafkaSettings, KafkaProducerSettings.class.getClassLoader());
    }
}
