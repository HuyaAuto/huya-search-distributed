package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.KafkaProducerSettings;
import com.huya.search.facing.producer.*;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/14.
 */
public class ProducerServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Kafka-Producer")).to(KafkaProducerSettings.class).in(Singleton.class);
        bind(ProducerService.class).to(KafkaProducerService.class).in(Singleton.class);
        bind(ProducerCommand.class).to(ProducerCommandImpl.class).in(Singleton.class);
        bind(Producer.class).to(KafkaProducer.class).in(Singleton.class);
    }
}