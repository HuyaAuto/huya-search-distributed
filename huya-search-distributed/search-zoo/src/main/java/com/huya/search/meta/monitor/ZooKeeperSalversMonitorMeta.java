package com.huya.search.meta.monitor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.meta.MetaContainer;
import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.MonitorMeta;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/24.
 *
 * 维持整个系统除node模块外元数据的源头 metaContainer，
 * 改变 metaContainer 将会改变系统的 MetaFollower 服务
 */
@Singleton
public class ZooKeeperSalversMonitorMeta extends MonitorMeta {

    private MetaContainer metaContainer;

    @Inject
    public ZooKeeperSalversMonitorMeta(MetaContainer metaContainer) {
        this.metaContainer = metaContainer;
    }

    @Override
    public String tag() {
        return "ZooKeeperSalversMonitorMeta";
    }

    @Override
    protected void addSelf(TimelineMetaDefine metaDefine) {
        metaContainer.add(metaDefine);
    }

    @Override
    protected void updateSelf(String table, MetaDefine metaDefine) {
        TimelineMetaDefine oldMetaDefine = metaContainer.get(table);
        oldMetaDefine.add(metaDefine);
    }

    @Override
    protected void updateSelf(TimelineMetaDefine metaDefine) {
        metaContainer.add(metaDefine);
    }

    @Override
    protected void removeSelf(String table) {
        metaContainer.remove(table);
    }

    public Set<String> getTables() {
        return metaContainer.getTables();
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        return metaContainer.iterator();
    }

    @Override
    public TimelineMetaDefine getTimelineMetaDefine(String table) {
        return metaContainer.get(table);
    }
}
