package com.huya.facing.convert;

import com.huya.search.IndexSettings;
import com.huya.search.facing.convert.DataConvert;
import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.index.analyzer.DynamicAnalyzer;
import com.huya.search.index.data.SearchDataRow;
import groovy.lang.GroovyCodeSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static com.huya.search.facing.subscriber.KafkaShardConsumer.GROOVY_CLASS_LOADER;

/**
 * @author ZhangXueJun
 * @date 2018年03月26日
 */
public class TafLogrDataTest {

    private String topic = "beelogsvr_taf";
    private int shardId = 0;

    @Test
    public void runTest() throws IOException, IgnorableConvertException, IllegalAccessException, InstantiationException {
//        DynamicAnalyzer settings = IndexSettings.getDynamicAnalyzer("", "", "");

        Properties consumerProps = new Properties();
        consumerProps.put("group.id", "test06666666");
        consumerProps.put("enable.auto.commit", "false");
//        consumerProps.put("auto.commit.interval.ms", 1000);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("max.partition.fetch.bytes", 52428800);
        consumerProps.put("max.poll.records", 50);
//        consumerProps.put("receive.buffer.bytes", 65536);
        consumerProps.put("auto.offset.reset", "latest");
        consumerProps.setProperty("bootstrap.servers", "foshan0-kafka-beelogsvr.huya.com:9092,foshan1-kafka-beelogsvr.huya.com:9092,foshan2-kafka-beelogsvr.huya.com:9092,foshan3-kafka-beelogsvr.huya.com:9092");
        consumerProps.put("security.protocol", "SASL_PLAINTEXT");
        consumerProps.put("sasl.mechanism", "PLAIN");
        System.setProperty("java.security.auth.login.config", "E:/other/huya-search/huya-search-distributed/temp/conf/kafka_client_jaas.conf"); // 环境变量添加，需要输入配置文件的路径
        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerProps);
//        consumer.subscribe(Arrays.asList(topic));

        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        Set<TopicPartition> partitions = new HashSet<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            TopicPartition actualTopicPartition = new TopicPartition(topic, partitionInfo.partition());
            partitions.add(actualTopicPartition);


        }

        consumer.assign(partitions);


        for (TopicPartition partition : partitions) {
            Long actualEndOffset = consumer.endOffsets(partitions).get(partition);
            long actualPosition = consumer.position(partition);
            System.out.println(partition.partition() + ":" + actualEndOffset);
        }




//        this.consumer.close();

//        this.consumer = new KafkaConsumer<>(this.properties);

        Class groovyClass = GROOVY_CLASS_LOADER.parseClass(
                new GroovyCodeSource(Objects.requireNonNull(
                        this.getClass().getClassLoader().getResource("convert/Taf_log_dataConvert.groovy")))
        );

        DataConvert<byte[], byte[], SearchDataRow> convert = (DataConvert<byte[], byte[], SearchDataRow>) groovyClass.newInstance();

        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(100);
            if (records == null || records.isEmpty()) {
                continue;
            }


            for (ConsumerRecord<byte[], byte[]> record : records) {
                SearchDataRow searchDataRow = convert.convert(0, 0, record.value(), record.key());
//                System.out.println(searchDataRow);
            }
        }
    }

    public static void main(String[] args) {
        String fileName = "/data/yygamelive/card_package_dbgwdb/accountlog/card_package_dbgwdb_account_201803260900.log";
        int index = fileName.lastIndexOf("/");
        String dir = fileName.substring(0, index);
        String name = fileName.substring(index + 1, fileName.length());
        System.out.println();
    }
}
