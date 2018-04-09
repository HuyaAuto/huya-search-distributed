package com.huya.search.facing.producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/8.
 */
public class LocalPartitioner implements Partitioner {

    public static int i = 0;

    public static final int partitionSize = 4;

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        return (i++) % partitionSize;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
