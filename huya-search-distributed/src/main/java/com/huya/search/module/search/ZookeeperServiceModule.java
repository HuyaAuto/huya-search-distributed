package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.NodeSettings;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/8.
 */
public class ZookeeperServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Node")).to(NodeSettings.class).in(Singleton.class);
    }
}
