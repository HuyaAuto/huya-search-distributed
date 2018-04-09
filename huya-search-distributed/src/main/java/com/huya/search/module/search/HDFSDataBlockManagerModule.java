package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.huya.search.index.block.*;
import com.huya.search.memory.*;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class HDFSDataBlockManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataBlocks.class).to(HDFSDataBlocks.class).in(Singleton.class);
        bind(PartitionsFactory.class).to(DistributedPartitionsFactory.class).in(Singleton.class);
        bind(ShardsFactory.class).to(IndexShardsFactory.class).in(Singleton.class);
        bind(MemoryControl.class).to(RealMemoryControlService.class).in(Singleton.class);
        bind(MemoryMonitor.class).to(RealMemoryMonitor.class).in(Singleton.class);
        bind(MemoryStrategy.class).to(DefaultMemoryStrategy.class).in(Singleton.class);
    }
}
