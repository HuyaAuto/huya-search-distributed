package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.huya.search.index.block.DataBlockManager;
import com.huya.search.index.block.HDFSDataBlockManager;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public class IndexEngineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataBlockManager.class).to(HDFSDataBlockManager.class).in(Singleton.class);
    }

}
