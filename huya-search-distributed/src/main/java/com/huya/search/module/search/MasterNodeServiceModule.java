package com.huya.search.module.search;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.huya.search.KafkaSubscriberSettings;
import com.huya.search.NodeSettings;
import com.huya.search.facing.KafkaInfoService;
import com.huya.search.index.block.TableShardServiceAble;
import com.huya.search.index.meta.MetaContainer;
import com.huya.search.index.meta.RealMetaService;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.meta.monitor.MonitorOriginalMeta;
import com.huya.search.index.meta.monitor.MySQLMonitorMeta;
import com.huya.search.meta.MasterMetaService;
import com.huya.search.meta.RealMasterMetaService;
import com.huya.search.meta.RealSalversMetaService;
import com.huya.search.meta.SalversMetaService;
import com.huya.search.meta.monitor.SalversMonitorMeta;
import com.huya.search.meta.monitor.ZooKeeperMasterMonitorMeta;
import com.huya.search.node.*;
import com.huya.search.node.sep.MasterNodeService;
import com.huya.search.node.sep.SearchMasterNodeService;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class MasterNodeServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Settings.class).annotatedWith(Names.named("Node")).to(NodeSettings.class).in(Singleton.class);
        bind(MasterMetaService.class).to(RealMasterMetaService.class).in(Singleton.class);
        bind(MonitorOriginalMeta.class).annotatedWith(Names.named("MySQLMeta")).to(MySQLMonitorMeta.class).in(Singleton.class);
        bind(Settings.class).annotatedWith(Names.named("Kafka-Subscriber")).to(KafkaSubscriberSettings.class).in(Singleton.class);
        bind(MonitorMeta.class).annotatedWith(Names.named("MasterMonitorMeta")).to(ZooKeeperMasterMonitorMeta.class).in(Singleton.class);
        bind(SalversMetaService.class).to(RealSalversMetaService.class).in(Singleton.class);
        bind(MonitorMeta.class).annotatedWith(Names.named("MetaFollowerIt")).to(SalversMonitorMeta.class).in(Singleton.class);
        bind(MetaContainer.class).to(RealMetaService.class).in(Singleton.class);
        bind(TasksBox.class).to(TasksBoxImpl.class).in(Singleton.class);

        bind(SalverNodeFactory.class).to(RealSalverNodeFactory.class).in(Singleton.class);
        bind(Cluster.class).to(RealCluster.class).in(Singleton.class);
        bind(TaskManager.class).to(HumanTaskManager.class).in(Singleton.class);
        bind(TaskDistributionService.class).to(RealTaskDistributionService.class).in(Singleton.class);
        bind(MasterNodeService.class).to(SearchMasterNodeService.class).in(Singleton.class);
        bind(TableShardServiceAble.class).to(KafkaInfoService.class).in(Singleton.class);

    }
}
