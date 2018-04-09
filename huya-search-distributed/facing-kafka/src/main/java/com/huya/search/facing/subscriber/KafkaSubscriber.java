package com.huya.search.facing.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huya.search.index.Engine;
import com.huya.search.index.block.ShardInfos;
import com.huya.search.index.block.ShardInfosService;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

public class KafkaSubscriber implements Subscriber {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaSubscriber.class);

    private Vector<ShardConsumer> shardConsumerVector;

    private ShardInfos shardInfos;  //todo 可能以后会增加更多信息

    private String table;

    private String topic;

    private String kafkaBootstrapServers;

    private String indexDeserializer;

    private KafkaSubscriberShard subscriberShard = KafkaSubscriberShard.newInstance();

    public KafkaSubscriber(Properties properties, Engine engine, TimelineMetaDefine metaDefine, ExecutorService es, ShardInfosService shardInfosService) {
        this.kafkaBootstrapServers = metaDefine.getKafkaBootstrapServers();
        properties.setProperty("bootstrap.servers", this.kafkaBootstrapServers);

        subscriberShard.setProperties(properties)
                .setEngine(engine)
                .setMetaDefine(metaDefine)
                .setEs(es);
        this.table = metaDefine.getTable();
        this.topic = metaDefine.getTopic();
        this.kafkaBootstrapServers = metaDefine.getKafkaBootstrapServers();
        this.indexDeserializer = metaDefine.getIndexDeserializer();
        this.shardInfos = shardInfosService.getShardInfos();
        initShardsVector(shardInfos.shardNum());
    }

    private void initShardsVector(int num) {
        shardConsumerVector = new Vector<>();
        for (int i = 0; i < num; i++) {
            shardConsumerVector.add(KafkaShardConsumer.newInstance(subscriberShard, i));
        }
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    @Override
    public String getIndexDeserializer() {
        return indexDeserializer;
    }

    @Override
    public boolean isRun() {
        for (ShardConsumer shardConsumer : shardConsumerVector) {
            if (shardConsumer.isRun()) return true;
        }
        return false;
    }

    @Override
    public boolean isRun(int shardId) {
        ShardConsumer shardConsumer = shardConsumerVector.get(shardId);
        return shardConsumer != null && shardConsumer.isRun();
    }

    @Override
    public synchronized void startKafkaConsumer(int shardId) {
        ShardConsumer shardConsumer = shardConsumerVector.get(shardId);
        if (!shardConsumer.isRun()) {
            shardConsumer.start();
        }
    }

    @Override
    public synchronized void startKafkaConsumerFromEnd(int shardId) {
        ShardConsumer shardConsumer = shardConsumerVector.get(shardId);
        if (!shardConsumer.isRun()) {
            shardConsumer.startFromEnd();
        }
    }

    @Override
    public synchronized void stopKafkaConsumer(int shardId) {
        ShardConsumer shardConsumer = shardConsumerVector.get(shardId);
        if (shardConsumer.isRun()) {
            shardConsumer.stop();
        }
    }

    @Override
    public synchronized void closeKafkaConsumer(int shardId) {
        ShardConsumer shardConsumer = shardConsumerVector.get(shardId);
        shardConsumer.close();
    }

    @Override
    public synchronized void close() {
        for (ShardConsumer shardConsumer : shardConsumerVector) {
            LOG.info("this table {} will close, contain shardIds : {}", table, shardConsumer.shardId());
            shardConsumer.close();
        }
    }

    @Override
    public JsonNode syncPullTask() {
        ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
        for (ShardConsumer shardConsumer : shardConsumerVector) {
            if (shardConsumer.isRun()) {
                arrayNode.add(shardConsumer.shardId());
            }
        }
        return arrayNode;
    }

}
