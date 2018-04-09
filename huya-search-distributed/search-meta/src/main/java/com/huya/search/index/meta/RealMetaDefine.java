package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.util.JodaUtil;
import com.huya.search.util.JsonUtil;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/14.
 */
public class RealMetaDefine implements MetaDefine {

    private static final IndexFeatureType ID = IndexFeatureType.newInstance(IndexFieldType.String, false, null);

    private static final IndexFeatureType OFFSET = IndexFeatureType.newInstance(IndexFieldType.Long, true, null);

    private static final IndexFeatureType TIMESTAMP = IndexFeatureType.newInstance(IndexFieldType.Long, true, null);

    private PartitionCycle partitionCycle;

    private Map<String, IndexFeatureType> map;

    private Map<String, IndexFeatureType> all = new HashMap<>();

    RealMetaDefine(PartitionCycle cycle, Map<String, IndexFeatureType> map) {
        this.partitionCycle = cycle;
        this.map = map;
        this.all.putAll(map);
        this.all.put(MetaEnum.ID, ID);
        this.all.put(MetaEnum.OFFSET, OFFSET);
        this.all.put(MetaEnum.TIMESTAMP, TIMESTAMP);
    }

    @Override
    public IndexFeatureType getIndexFieldFeatureType(String name) {
        IndexFeatureType indexFeatureType = all.get(name);
        return indexFeatureType == null ? defaultIndexFieldFeatureType() : indexFeatureType;
    }

    @Override
    public IndexFeatureType defaultIndexFieldFeatureType() {
        return IndexFeatureType.DEFAULT_FEATURE_TYPE;
    }

    @Override
    public TimelineMetaDefine tran(MetaHeadInfo metaHeadInfo) {
        TreeMap<Long, MetaDefine> treeMap = new TreeMap<>();
        treeMap.put(this.partitionCycle.getFloor(), this);
        return new RealTimelineMetaDefine(metaHeadInfo, treeMap);
    }

    @Override
    public PartitionGrain getGrain() {
        return partitionCycle.getGrain();
    }

    @Override
    public Map<String, IndexFeatureType> getMap() {
        return map;
    }

    @Override
    public Map<String, IndexFeatureType> getAll() {
        return all;
    }

    @Override
    public PartitionCycle getFirstPartitionCycle() {
        return partitionCycle;
    }

    @Override
    public String getFirstTimestamp() {
        return JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER.print(partitionCycle.getFloor());
    }

    @Override
    public String getMetaDefineTimestampKey() {
        return partitionCycle.partitionName();
    }

    @Override
    public ObjectNode toObject() {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        map.forEach((key, value) -> objectNode.set(key, value.toObject()));
        return objectNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealMetaDefine that = (RealMetaDefine) o;

        if (!partitionCycle.equals(that.partitionCycle)) return false;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        int result;
        result = partitionCycle.hashCode();
        result = 31 * result + map.hashCode();
        return result;
    }


}
