package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.util.PathUtil;

import java.io.File;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/31.
 */
public class ShardMetaDefine implements IntactMetaDefine {

    private final IntactMetaDefine intactMetaDefine;

    private final PartitionCycle cycle;

    private final int shardId;

    public static ShardMetaDefine create(IntactMetaDefine intactMetaDefine, PartitionCycle cycle, int shardId) {
        return new ShardMetaDefine(intactMetaDefine, cycle, shardId);
    }

    private ShardMetaDefine(IntactMetaDefine intactMetaDefine, PartitionCycle cycle, int shardId) {
        this.intactMetaDefine = intactMetaDefine;
        this.cycle = cycle;
        this.shardId = shardId;
    }

    @Override
    public IndexFeatureType getIndexFieldFeatureType(String name) {
        return intactMetaDefine.getIndexFieldFeatureType(name);
    }

    @Override
    public IndexFeatureType defaultIndexFieldFeatureType() {
        return intactMetaDefine.defaultIndexFieldFeatureType();
    }

    @Override
    public TimelineMetaDefine tran(MetaHeadInfo metaHeadInfo) {
        return intactMetaDefine.tran(metaHeadInfo);
    }

    @Override
    public PartitionGrain getGrain() {
        return intactMetaDefine.getGrain();
    }

    @Override
    public Map<String, IndexFeatureType> getMap() {
        return intactMetaDefine.getMap();
    }

    @Override
    public Map<String, IndexFeatureType> getAll() {
        return intactMetaDefine.getAll();
    }

    @Override
    public PartitionCycle getFirstPartitionCycle() {
        return intactMetaDefine.getFirstPartitionCycle();
    }

    @Override
    public String getFirstTimestamp() {
        return intactMetaDefine.getFirstTimestamp();
    }

    @Override
    public String getMetaDefineTimestampKey() {
        return intactMetaDefine.getMetaDefineTimestampKey();
    }

    @Override
    public String getTable() {
        return intactMetaDefine.getTable();
    }

    @Override
    public String getIndexDeserializer() {
        return intactMetaDefine.getIndexDeserializer();
    }

    @Override
    public String getTopic() {
        return intactMetaDefine.getTopic();
    }

    @Override
    public String getKafkaBootstrapServers() {
        return intactMetaDefine.getKafkaBootstrapServers();
    }

    @Override
    public double accuracy() {
        return intactMetaDefine.accuracy();
    }

    @Override
    public ObjectNode toObject() {
        return intactMetaDefine.toObject();
    }

    public PartitionCycle getPartitionCycle() {
        return cycle;
    }

    public int getShardId() {
        return shardId;
    }

    public String path() {
        return getTable() + PathUtil.separator + shardId + PathUtil.separator + cycle.partitionName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShardMetaDefine that = (ShardMetaDefine) o;

        if (shardId != that.shardId) return false;
        if (!intactMetaDefine.equals(that.intactMetaDefine)) return false;
        return cycle.equals(that.cycle);
    }

    @Override
    public int hashCode() {
        int result = intactMetaDefine.hashCode();
        result = 31 * result + cycle.hashCode();
        result = 31 * result + shardId;
        return result;
    }

    @Override
    public String toString() {
        return "ShardMetaDefine{" +
                "table=" + intactMetaDefine.getTable() +
                ", cycle=" + cycle.partitionName() +
                ", shardId=" + shardId +
                '}';
    }

}
