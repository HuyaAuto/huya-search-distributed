package com.huya.search.index.meta;

import java.util.TreeMap;

public class TimelineMetaDefineBuilder {

    public static TimelineMetaDefineBuilder newInstance() {
        return new TimelineMetaDefineBuilder();
    }

    private TimelineMetaDefineBuilder() {}

    private TreeMap<Long, MetaDefine> treeMap = new TreeMap<>();

    private MetaHeadInfo metaHeadInfo;

    public TimelineMetaDefineBuilder addMetaDefine(MetaDefine metaDefine) {
        this.treeMap.put(metaDefine.getFirstPartitionCycle().getFloor(), metaDefine);
        return this;
    }

    public TimelineMetaDefineBuilder addMetaHeadInfo(MetaHeadInfo metaHeadInfo) {
        this.metaHeadInfo = metaHeadInfo;
        return this;
    }

    public TimelineMetaDefine build() {
        return new RealTimelineMetaDefine(metaHeadInfo, treeMap);
    }
}
