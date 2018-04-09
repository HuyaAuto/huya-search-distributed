package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.util.JsonUtil;

import java.util.Map;
import java.util.Objects;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/12.
 */
public class RealIntactMetaDefine implements IntactMetaDefine {

    public static RealIntactMetaDefine newInstance(MetaHeadInfo metaHeadInfo, MetaDefine metaDefine) {
        return new RealIntactMetaDefine(metaHeadInfo, metaDefine);
    }

    private MetaHeadInfo metaHeadInfo;

    private MetaDefine metaDefine;

    private RealIntactMetaDefine(MetaHeadInfo metaHeadInfo, MetaDefine metaDefine) {
        this.metaHeadInfo = metaHeadInfo;
        this.metaDefine = metaDefine;
    }


    @Override
    public String getIndexDeserializer() {
        return metaHeadInfo.getIndexDeserializer();
    }

    @Override
    public String getTopic() {
        return metaHeadInfo.getTopic();
    }

    @Override
    public String getKafkaBootstrapServers() {
        return metaHeadInfo.getKafkaBootstrapServers();
    }

    @Override
    public double accuracy() {
        return metaHeadInfo.accuracy();
    }

    @Override
    public IndexFeatureType getIndexFieldFeatureType(String name) {
        return metaDefine.getIndexFieldFeatureType(name);
    }

    @Override
    public IndexFeatureType defaultIndexFieldFeatureType() {
        return metaDefine.defaultIndexFieldFeatureType();
    }

    @Override
    public TimelineMetaDefine tran(MetaHeadInfo metaHeadInfo) {
        return metaDefine.tran(metaHeadInfo);
    }

    @Override
    public PartitionGrain getGrain() {
        return metaDefine.getGrain();
    }

    @Override
    public Map<String, IndexFeatureType> getMap() {
        return metaDefine.getMap();
    }

    @Override
    public Map<String, IndexFeatureType> getAll() {
        return metaDefine.getAll();
    }

    @Override
    public PartitionCycle getFirstPartitionCycle() {
        return metaDefine.getFirstPartitionCycle();
    }

    @Override
    public String getFirstTimestamp() {
        return metaDefine.getFirstTimestamp();
    }

    @Override
    public String getMetaDefineTimestampKey() {
        return metaDefine.getMetaDefineTimestampKey();
    }

    @Override
    public String getTable() {
        return metaHeadInfo.getTable();
    }

    @Override
    public ObjectNode toObject() {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        objectNode.put(MetaEnum.TABLE, getTable());
        objectNode.put(MetaEnum.TOPIC, getTopic());
        objectNode.put(MetaEnum.INDEX_DESERIALIZER, getIndexDeserializer());
        objectNode.put(MetaEnum.GRAIN, getGrain().getGrain());
        objectNode.put(MetaEnum.ACCURACY, accuracy());
        objectNode.set(metaDefine.getMetaDefineTimestampKey(), metaDefine.toObject());
        return objectNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealIntactMetaDefine that = (RealIntactMetaDefine) o;
        return Objects.equals(metaHeadInfo, that.metaHeadInfo) &&
                Objects.equals(metaDefine, that.metaDefine);
    }

    @Override
    public int hashCode() {

        return Objects.hash(metaHeadInfo, metaDefine);
    }


}
