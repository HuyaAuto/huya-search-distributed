package com.huya.search.index.meta.monitor;

import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/19.
 */
public class MapMonitorMeta extends MonitorMeta {

    private Map<String, TimelineMetaDefine> map = new ConcurrentHashMap<>();

    private String tag;

    public MapMonitorMeta(String tag) {
        this.tag = tag;
    }

    @Override
    public String tag() {
        return tag;
    }

    @Override
    protected void addSelf(TimelineMetaDefine metaDefine) {
        updateSelf(metaDefine);
    }

    @Override
    protected void updateSelf(String table, MetaDefine metaDefine) {
        TimelineMetaDefine oldMetaDefine = map.get(table);
        assert oldMetaDefine != null;
        oldMetaDefine.add(metaDefine);
    }

    @Override
    protected void updateSelf(TimelineMetaDefine metaDefine) {
        map.put(metaDefine.getTable(), metaDefine);
    }

    @Override
    protected void removeSelf(String table) {
        map.remove(table);
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        return map.values().iterator();
    }

    @Override
    public TimelineMetaDefine getTimelineMetaDefine(String table) {
        return map.get(table);
    }
}
