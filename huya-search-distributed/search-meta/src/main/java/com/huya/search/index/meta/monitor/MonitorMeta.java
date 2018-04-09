package com.huya.search.index.meta.monitor;

import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/19.
 */
public abstract class MonitorMeta {

    private Map<String, MonitorMeta> monitorMap = new HashMap<>();

    public abstract String tag();

    public void bind(MonitorMeta monitorMeta) {
        if (! monitorMap.keySet().contains(monitorMeta.tag())) {
            monitorMap.put(monitorMeta.tag(), monitorMeta);
        }
    }

    public void unBind(String tag) {
        monitorMap.remove(tag);
    }

    public void unBind(MonitorMeta monitorMeta) {
        unBind(monitorMeta.tag());
    }

    public void add(TimelineMetaDefine metaDefine) {
        addSelf(metaDefine);
        monitorMap.values().forEach(m -> m.add(metaDefine));
    }

    protected abstract void addSelf(TimelineMetaDefine metaDefine);

    public void update(String table, MetaDefine metaDefine) {
        updateSelf(table, metaDefine);
        monitorMap.values().forEach(m -> m.update(table, metaDefine));
    }

    protected abstract void updateSelf(String table, MetaDefine metaDefine);

    public void update(TimelineMetaDefine metaDefine) {
        updateSelf(metaDefine);
        monitorMap.values().forEach(m -> m.update(metaDefine));
    }

    protected abstract void updateSelf(TimelineMetaDefine metaDefine);

    public void remove(String table) {
        removeSelf(table);
        monitorMap.values().forEach(m -> m.remove(table));
    }

    protected abstract void removeSelf(String table);

    public abstract Iterator<TimelineMetaDefine> iterator();

    public abstract TimelineMetaDefine getTimelineMetaDefine(String table);

    protected Map<String, MonitorMeta> getMonitorMap() {
        return monitorMap;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
