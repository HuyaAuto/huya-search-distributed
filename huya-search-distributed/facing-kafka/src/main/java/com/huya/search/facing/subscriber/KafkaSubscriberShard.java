package com.huya.search.facing.subscriber;

import com.huya.search.index.Engine;
import com.huya.search.index.meta.TimelineMetaDefine;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/3.
 */
public class KafkaSubscriberShard {

    private Properties properties;

    private Engine engine;

    private TimelineMetaDefine metaDefine;

    private ExecutorService es;

    public static KafkaSubscriberShard newInstance() {
        return new KafkaSubscriberShard();
    }

    public Properties getProperties() {
        return properties;
    }

    public KafkaSubscriberShard setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public Engine getEngine() {
        return engine;
    }

    public KafkaSubscriberShard setEngine(Engine engine) {
        this.engine = engine;
        return this;
    }

    public TimelineMetaDefine getMetaDefine() {
        return metaDefine;
    }

    public KafkaSubscriberShard setMetaDefine(TimelineMetaDefine metaDefine) {
        this.metaDefine = metaDefine;
        return this;
    }

    public ExecutorService getEs() {
        return es;
    }

    public KafkaSubscriberShard setEs(ExecutorService es) {
        this.es = es;
        return this;
    }
}
