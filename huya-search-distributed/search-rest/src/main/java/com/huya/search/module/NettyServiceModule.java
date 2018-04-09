package com.huya.search.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.restful.NettySettings;
import com.huya.search.settings.Settings;

public class NettyServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Netty")).to(NettySettings.class).in(Singleton.class);
    }
}
