package com.huya.search.misc.kafka.transfer;

import com.duowan.datawarehouse.model.ActionLog;
import com.duowan.datawarehouse.utils.ActionLogParser;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 中转kafka，用于适配facing-kafka接口，一个offset对应yige record。应用场景：huya_action_log等批量
 *
 * @author ZhangXueJun
 * @date 2018年03月20日
 */
public class KafkaTransfer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransfer.class);

    private String topic;
    private String groupId;
    private String srcServers;
    private String targetServers;

    private Producer producer;

    private int threadNum;

    /**
     * ./start.sh huya_action_log huya_action_log_transfer1 s0-ds-kafak.huya.com:9092,s1-ds-kafak.huya.com:9092,s2-ds-kafak.huya.com:9092 kafka01:9092,kafka02:9092 18 &
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("args.size" + args.length + " " + Arrays.toString(args));
        // 10.64.66.161  s0-ds-kafak.huya.com
//        10.64.66.162  s1-ds-kafak.huya.com
//        10.64.66.173  s2-ds-kafak.huya.com
//        topic：
//        huya_action_log
//                yy_mbsdkevent_original
        KafkaTransfer transfer = new KafkaTransfer(args[0], args[1], args[2], args[3], Integer.valueOf(args[4]));
        transfer.transfer();

        while (true) {
            try {
                Thread.sleep(1000L * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public KafkaTransfer(String topic, String groupId, String srcServers, String targetServers, int threadNum) {
        this.topic = topic;
        this.groupId = groupId;
        this.srcServers = srcServers;
        this.targetServers = targetServers;
        this.threadNum = threadNum;
    }

    public Producer getProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", targetServers);
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("batch.size", 163840);
        props.put("linger.ms", 0);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

//        System.setProperty("java.security.auth.login.config", "E:/other/huya-search/huya-search-distributed/temp/conf/kafka_client_jaas.conf"); // 环境变量添加，需要输入配置文件的路径
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        if (producer == null) {
            producer = new KafkaProducer<String, String>(props);
        }
        return producer;
    }

    private final AtomicLong atomicLong = new AtomicLong(0);

    private void transfer() {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", srcServers);
        consumerProps.put("group.id", groupId);
        consumerProps.put("enable.auto.commit", "false");
//        consumerProps.put("auto.commit.interval.ms", 1000);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("max.partition.fetch.bytes", 52428800);
        consumerProps.put("max.poll.records", 50);
//        consumerProps.put("receive.buffer.bytes", 65536);
        consumerProps.put("auto.offset.reset", "latest");
        logger.info("调式新泽西======================================");

        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(() ->
            {
                KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerProps);
                consumer.subscribe(Arrays.asList(topic));

                while (true) {
//                    logger.info("进入wile循环.....................");
                    ConsumerRecords<byte[], byte[]> records = consumer.poll(100);
                    if (records == null || records.isEmpty()) {
                        continue;
                    }

//                    logger.info("接收到消息.....................");
                    for (ConsumerRecord<byte[], byte[]> record : records) {
                        try {
                            List<ActionLog> actionLogs = ActionLogParser.parseHiidoPBToActionLogs(record.value(), false, null);
                            for (ActionLog actionLog : actionLogs) {
                                actionLog.setExt(null);
                                ProducerRecord<String, String> msg = new ProducerRecord<>(topic, actionLog.toString());
                                getProducer().send(msg);
                            }
                            atomicLong.getAndAdd(actionLogs.size());
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
            , "thread-" + i);
            thread.start();
        }

        Thread monitorThread = new Thread(() -> {
            while (true) {
                logger.info("累积消费====================================" + atomicLong);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        monitorThread.start();
    }
}