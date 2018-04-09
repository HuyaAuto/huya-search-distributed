package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.IndexSettings;
import com.huya.search.index.meta.JsonFileMetaHandle;
import com.huya.search.index.meta.MetaHandle;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/15.
 */
public class MetaServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Meta")).to(IndexSettings.class).in(Singleton.class);
        bind(MetaService.class).to(RealMetaService.class).in(Singleton.class);
    }
}
