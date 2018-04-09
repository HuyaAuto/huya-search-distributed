package com.huya.search.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.KafkaProducerSettings;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.settings.Settings;

public class KafkaProducerServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Kafka-Producer")).to(KafkaProducerSettings.class).in(Singleton.class);
        bind(MetaService.class).to(RealMetaService.class).in(Singleton.class);
    }
}
