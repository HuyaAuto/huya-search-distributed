package com.huya.search.facing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.index.block.ShardInfos;
import com.huya.search.index.block.ShardInfosService;
import com.huya.search.index.block.TableShardServiceAble;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.settings.Settings;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/25.
 */
@Singleton
public class KafkaInfoService extends TableShardServiceAble {

    private Properties properties;

    private MetaService metaService;

    private Map<String, ShardInfosService> infoCacheMap = new HashMap<>();


    @Inject
    public KafkaInfoService(@Named("Kafka-Subscriber") Settings settings, MetaService metaService) {
        this.properties = settings.asProperties();
        this.metaService = metaService;
    }

    public synchronized ShardInfosService init(String table) {
        ShardInfosService shardInfosService = infoCacheMap.get(table);
        if (shardInfosService == null) {
            TimelineMetaDefine metaDefine = metaService.get(table);
            String topic = metaDefine.getTopic();
            String kafkaBootstrapServers = metaDefine.getKafkaBootstrapServers();
            properties.setProperty("bootstrap.servers", kafkaBootstrapServers);
            KafkaConsumer<String, ?> consumer = new KafkaConsumer<>(properties);
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
            shardInfosService = new DefaultShardInfos(table, partitionInfos);
            infoCacheMap.put(table, shardInfosService);
            consumer.close();
        }
        return shardInfosService;
    }

    private List<Double> initTpsList(String topic, int num, Map<TopicPartition, OffsetAndTimestamp> beginOffsetAndTimestampMap, Map<TopicPartition, OffsetAndTimestamp> endOffsetAndTimestampMap) {
        List<Double> tpsList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            TopicPartition topicPartition = new TopicPartition(topic, i);
            OffsetAndTimestamp beginOffsetAndTimestamp = beginOffsetAndTimestampMap.get(topicPartition);
            OffsetAndTimestamp endOffsetAndTimestamp   = endOffsetAndTimestampMap.get(topicPartition);
            if (beginOffsetAndTimestamp != null && endOffsetAndTimestamp != null) {
                long offset = endOffsetAndTimestamp.offset() - beginOffsetAndTimestamp.offset();
                long timestamp = endOffsetAndTimestamp.timestamp() - beginOffsetAndTimestamp.timestamp();
                tpsList.add((double) (offset / timestamp));
            }
            else {
                tpsList.add(-1d);
            }
        }
        return tpsList;
    }

    private Collection<TopicPartition> getTopPartitionList(String topic, int num) {
        List<TopicPartition> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(new TopicPartition(topic, i));
        }
        return list;
    }

    @Override
    protected void doStart() throws SearchException {
        //do nothing
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        //do nothing
    }

    @Override
    public String getName() {
        return "KafkaInfoService";
    }

    /**
     * 获取分片信息服务
     * @param table 表名
     * @return 分片信息服务
     */
    @Override
    public ShardInfosService getShardInfos(String table) {
        return init(table);
    }

    private class DefaultShardInfos implements ShardInfosService {

        private String table;

        private List<PartitionInfo> partitionInfos;

        private DefaultShardInfos(String table, List<PartitionInfo> partitionInfos) {
            this.table = table;
            this.partitionInfos = partitionInfos;
        }

        public String getTable() {
            return table;
        }

        @Override
        public ShardInfos getShardInfos() {
            return () -> partitionInfos.size();
        }
    }
}
