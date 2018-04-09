package com.huya.search;

import com.huya.search.facing.subscriber.TakeScoopSubscriber;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author ZhangXueJun
 * @date 2018年03月15日
 */
public class TakeScoopSubscriberTest {

    public static void main(String[] args) {
        args = new String[] {"Test", "0"};
        TakeScoopSubscriber.main(args);
    }

    @Test
    public void test01() {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "ip-14-29-58-119.yygamedev.com:9092");
        consumerProps.put("group.id", "test1");
        consumerProps.put("enable.auto.commit", "true");
        consumerProps.put("auto.commit.interval.ms", 1000);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("max.partition.fetch.bytes", 1048576);
        consumerProps.put("max.poll.records", 500);
        consumerProps.put("receive.buffer.bytes", 65536);
        consumerProps.put("auto.offset.reset", "latest");

        String topic = "Test";
        KafkaConsumer<String, String> consumer  = new KafkaConsumer<String, String>(consumerProps);
//        consumer .subscribe(Arrays.asList(topic));


        List<PartitionInfo> partitions = consumer.partitionsFor(topic);
//        this.consumer.close();

//        this.consumer = new KafkaConsumer<>(this.properties);
        partitions.forEach(System.out::println);
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        System.out.println("start assign");

        consumer.assign(Collections.singletonList(topicPartition));
        consumer.seekToBeginning(Collections.singleton(topicPartition));
        System.out.println("start poll");

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (records == null || records.isEmpty()) {
                continue;
            }
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
        }
    }
}
