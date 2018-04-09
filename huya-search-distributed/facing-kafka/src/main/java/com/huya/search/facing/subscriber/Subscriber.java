package com.huya.search.facing.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.IndexDeserializer;
import com.huya.search.index.meta.CorrespondTable;
import com.huya.search.index.meta.CorrespondTopic;

public interface Subscriber extends CorrespondTable, CorrespondTopic, IndexDeserializer {

    boolean isRun();

    boolean isRun(int shardId);

    void startKafkaConsumer(int shardId);

    void startKafkaConsumerFromEnd(int shardId);

    void stopKafkaConsumer(int shardId);

    void closeKafkaConsumer(int shardId);

    void close();

    JsonNode syncPullTask();
}
