package com.huya.search.util;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.IndexSettings;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.settings.Settings;
import org.junit.Test;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/28.
 */
public class WarnTest {

    @Test
    public void runTest() {
        ModulesBuilder modules = ModulesBuilder.getInstance();
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Settings.class).annotatedWith(Names.named("Settings")).to(IndexSettings.class).in(Singleton.class);
            }
        });
        WarnService warnService = modules.createInjector().getInstance(WarnService.class);
        warnService.send("蛤蛤", new Exception("嘿嘿"));
    }
}
