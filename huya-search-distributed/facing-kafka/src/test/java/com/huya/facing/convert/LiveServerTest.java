package com.huya.facing.convert;

import com.huya.search.facing.convert.DataConvert;
import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.index.data.SearchDataRow;
import groovy.lang.GroovyCodeSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

import static com.huya.search.facing.subscriber.KafkaShardConsumer.GROOVY_CLASS_LOADER;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/8.
 */
public class LiveServerTest {

    @Test
    public void runConvert() throws IOException, IgnorableConvertException, IllegalAccessException, InstantiationException {
        Class groovyClass = GROOVY_CLASS_LOADER.parseClass(
                new GroovyCodeSource(Objects.requireNonNull(this.getClass().getClassLoader().getResource("convert/Live_serverConvert.groovy")))
        );

        Properties consumerProps = new Properties();
        consumerProps.put("group.id", "test05111");
        consumerProps.put("enable.auto.commit", "false");
//        consumerProps.put("auto.commit.interval.ms", 1000);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("max.partition.fetch.bytes", 52428800);
        consumerProps.put("max.poll.records", 50);
//        consumerProps.put("receive.buffer.bytes", 65536);
        consumerProps.put("auto.offset.reset", "latest");
        consumerProps.setProperty("bootstrap.servers", "foshan0-kafka-subscribe.huya.com:9092");
        consumerProps.put("security.protocol", "SASL_PLAINTEXT");
        consumerProps.put("sasl.mechanism", "PLAIN");
        System.setProperty("java.security.auth.login.config", "E:/other/huya-search/huya-search-distributed/temp/conf/kafka_client_jaas.conf"); // 环境变量添加，需要输入配置文件的路径
        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList("10006"));

        DataConvert<byte[], byte[], SearchDataRow> convert = (DataConvert<byte[], byte[], SearchDataRow>) groovyClass.newInstance();

        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(100);
            if (records == null || records.isEmpty()) {
                continue;
            }


            for (ConsumerRecord<byte[], byte[]> record : records) {
                SearchDataRow searchDataRow = convert.convert(0, 0, record.value(), record.key());
            }
        }
    }


}
