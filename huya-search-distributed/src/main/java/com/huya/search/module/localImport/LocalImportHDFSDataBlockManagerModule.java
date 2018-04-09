package com.huya.search.module.localImport;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.KafkaSubscriberSettings;
import com.huya.search.NodeSettings;
import com.huya.search.facing.KafkaInfoService;
import com.huya.search.index.block.*;
import com.huya.search.index.meta.MetaContainer;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.meta.monitor.MonitorOriginalMeta;
import com.huya.search.index.meta.monitor.MySQLMonitorMeta;
import com.huya.search.meta.monitor.SalversMonitorMeta;
import com.huya.search.settings.Settings;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class LocalImportHDFSDataBlockManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataBlocks.class).to(HDFSDataBlocks.class).in(Singleton.class);
        bind(PartitionsFactory.class).to(DistributedPartitionsFactory.class).in(Singleton.class);
        bind(ShardsFactory.class).to(LocalImportIndexShardsFactory.class).in(Singleton.class);
        bind(TableShardServiceAble.class).to(KafkaInfoService.class).in(Singleton.class);
        bind(Settings.class).annotatedWith(Names.named("Kafka-Subscriber")).to(KafkaSubscriberSettings.class).in(Singleton.class);
        bind(MonitorOriginalMeta.class).annotatedWith(Names.named("MySQLMeta")).to(MySQLMonitorMeta.class).in(Singleton.class);
        bind(Settings.class).annotatedWith(Names.named("Node")).to(NodeSettings.class).in(Singleton.class);
        bind(MonitorMeta.class).to(SalversMonitorMeta.class).in(Singleton.class);
        bind(MetaContainer.class).to(RealMetaService.class).in(Singleton.class);
    }
}
