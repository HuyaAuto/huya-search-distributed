package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.util.JsonUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/14.
 */
public class RealTimelineMetaDefine implements TimelineMetaDefine {

    private static final Logger LOG = LoggerFactory.getLogger(RealTimelineMetaDefine.class);

    private MetaHeadInfo metaHeadInfo;

    private TreeMap<Long, MetaDefine> metaDefineTreeMap;

    private volatile boolean open;

    RealTimelineMetaDefine(MetaHeadInfo metaHeadInfo, TreeMap<Long, MetaDefine> metaDefineTreeMap) {
        this.metaHeadInfo = metaHeadInfo;
        this.metaDefineTreeMap = metaDefineTreeMap;
        this.open = true;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void open() {
        open = true;
    }

    @Override
    public void close() {
        open = false;
    }

    @Override
    public IntactMetaDefine get(long unixTime) {
        LOG.info(unixTime + " " + metaDefineTreeMap.size()  + " " + metaDefineTreeMap.keySet().toString());
        return RealIntactMetaDefine.newInstance(metaHeadInfo, metaDefineTreeMap.floorEntry(unixTime).getValue());
    }

    @Override
    public IntactMetaDefine getLast() {
        return RealIntactMetaDefine.newInstance(metaHeadInfo, metaDefineTreeMap.lastEntry().getValue());
    }

    @Override
    public IntactMetaDefine getFirst() {
        return RealIntactMetaDefine.newInstance(metaHeadInfo, metaDefineTreeMap.firstEntry().getValue());
    }

    @Override
    public boolean exists(MetaDefine metaDefine) {
        return metaDefineTreeMap.containsValue(metaDefine);
    }

    @Override
    public ShardMetaDefine getMetaDefineByCycle(PartitionCycle cycle, int shardId) {
        IntactMetaDefine intactMetaDefine = RealIntactMetaDefine.newInstance(
                metaHeadInfo,
                get(cycle.getFloor())
        );
        return ShardMetaDefine.create(intactMetaDefine, cycle, shardId);
    }

    @Override
    public ShardMetaDefine getMetaDefineByUnixTime(long unixTime, int shardId) {
        return getMetaDefineByCycle(new PartitionCycle(unixTime, getGrain()), shardId);
    }

    @Override
    public synchronized void add(MetaDefine metaDefine) {
        //todo 未来支持动态分区粒度
        assert this.getGrain() == metaDefine.getGrain();
        PartitionCycle cycle = new PartitionCycle(new DateTime(), this.getGrain());
        metaDefineTreeMap.put(cycle.getFloor(), metaDefine);
    }

    @Override
    public List<PartitionCycle> getLastCycle(int n) {
        PartitionCycle cycle = new PartitionCycle(new DateTime(), getGrain());
        List<PartitionCycle> list = new ArrayList<>();
        list.add(cycle);
        PartitionCycle first = getFirstPartitionCycle();
        for (int i=0; i<n-1; i++) {
            cycle = cycle.prev();
            if (first.compareTo(cycle) <= 0) {
                list.add(cycle);
            }
        }
        return list;
    }

    @Override
    public IndexFeatureType getIndexFieldFeatureType(String name, long unixTime) {
        return get(unixTime).getMap().get(name);
    }

    @Override
    public IndexFeatureType getIndexFieldFeatureType(String name) {
        return getLast().getIndexFieldFeatureType(name);
    }

    @Override
    public IndexFeatureType defaultIndexFieldFeatureType() {
        return getLast().defaultIndexFieldFeatureType();
    }

    @Override
    public TimelineMetaDefine tran(MetaHeadInfo metaHeadInfo) {
        throw new RuntimeException("timelineMetaDefine no need to tran timelineMetaDefine");
    }

    @Override
    public PartitionGrain getGrain() {
        return getLast().getGrain();
    }

    @Override
    public Map<Long, MetaDefine> getTreeMap() {
        return metaDefineTreeMap;
    }

    @Override
    public Map<String, IndexFeatureType> getMap() {
        return getLast().getMap();
    }

    @Override
    public Map<String, IndexFeatureType> getAll() {
        return getLast().getAll();
    }

    @Override
    public PartitionCycle getFirstPartitionCycle() {
        return getFirst().getFirstPartitionCycle();
    }

    @Override
    public String getFirstTimestamp() {
        return getFirst().getFirstTimestamp();
    }

    @Override
    public String getMetaDefineTimestampKey() {
        return getLast().getMetaDefineTimestampKey();
    }

    @Override
    public double accuracy() {
        return metaHeadInfo.accuracy();
    }

    @Override
    public String getTable() {
        return metaHeadInfo.getTable();
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
    public String getIndexDeserializer() {
        return metaHeadInfo.getIndexDeserializer();
    }

    @Override
    public ObjectNode toObject() {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        objectNode.put(MetaEnum.TABLE, getTable());
        objectNode.put(MetaEnum.TOPIC, getTopic());
        objectNode.put(MetaEnum.KAFKA_BOOTSTRAP_SERVERS, getKafkaBootstrapServers());
        objectNode.put(MetaEnum.INDEX_DESERIALIZER, getIndexDeserializer());
        objectNode.put(MetaEnum.ACCURACY, accuracy());
        metaDefineTreeMap.forEach((key, value) -> {
            objectNode.set(value.getMetaDefineTimestampKey(), value.toObject());
        });
        return objectNode;
    }
}
