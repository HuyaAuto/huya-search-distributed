package com.huya.search.module.localImport;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.huya.search.facing.KafkaInfoService;
import com.huya.search.index.block.TableShardServiceAble;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
public class LocalImportServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TableShardServiceAble.class).to(KafkaInfoService.class).in(Singleton.class);
    }
}
