package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.IndexSettings;
import com.huya.search.KafkaSubscriberSettings;
import com.huya.search.NodeSettings;
import com.huya.search.facing.KafkaInfoService;
import com.huya.search.facing.subscriber.KafkaSubscriberService;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.index.Engine;
import com.huya.search.index.IndexEngine;
import com.huya.search.index.block.TableShardServiceAble;
import com.huya.search.index.meta.MetaContainer;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.meta.RealSalversMetaService;
import com.huya.search.meta.SalversMetaService;
import com.huya.search.meta.monitor.SalversMonitorMeta;
import com.huya.search.meta.monitor.ZooKeeperMasterMonitorMeta;
import com.huya.search.node.*;
import com.huya.search.node.sep.SalverNodeService;
import com.huya.search.node.sep.SearchSalverNodeService;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class SalverNodeServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Node")).to(NodeSettings.class).in(Singleton.class);
        bind(SalversMetaService.class).to(RealSalversMetaService.class).in(Singleton.class);
        bind(MonitorMeta.class).annotatedWith(Names.named("MetaFollowerIt")).to(SalversMonitorMeta.class).in(Singleton.class);
        bind(Settings.class).annotatedWith(Names.named("Tasks")).to(IndexSettings.class).in(Singleton.class);
        bind(MonitorMeta.class).annotatedWith(Names.named("MasterMonitorMeta")).to(ZooKeeperMasterMonitorMeta.class).in(Singleton.class);
        bind(Settings.class).annotatedWith(Names.named("Kafka-Subscriber")).to(KafkaSubscriberSettings.class).in(Singleton.class);
        bind(SubscriberService.class).to(KafkaSubscriberService.class).in(Singleton.class);
        bind(Engine.class).to(IndexEngine.class).in(Singleton.class);
        bind(MetaContainer.class).to(RealMetaService.class).in(Singleton.class);

        //search-zoo2  inject
        bind(SalverNodeFactory.class).to(RealSalverNodeFactory.class).in(Singleton.class);
        bind(SalverNodeService.class).to(SearchSalverNodeService.class).in(Singleton.class);
        bind(TableShardServiceAble.class).to(KafkaInfoService.class).in(Singleton.class);
    }
}
