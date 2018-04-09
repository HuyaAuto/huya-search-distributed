package com.huya.search.facing.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.huya.search.KafkaSubscriberSettings;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.settings.Settings;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Singleton
public class TakeScoopSubscriber implements Subscriber {

    private Settings settings;

    private Properties properties;

    private String topic;

    private String table;
    private String kafkaBootstrapServers;

    private KafkaConsumer<byte[], byte[]> consumer;


    @Inject
    public TakeScoopSubscriber(@Named("Kafka-Subscriber") Settings settings) {
        this.settings = settings;
        this.properties = getSubscriberSettings();
    }

    protected Properties getSubscriberSettings() {
        return settings.asProperties("search", false);
    }

    @Override
    public boolean isRun() {
        return false;
    }

    @Override
    public boolean isRun(int shardId) {
        return false;
    }

    @Override
    public void startKafkaConsumer(int shardId) {
//        properties.setProperty("bootstrap.servers", "ip-14-29-58-119.yygamedev.com:9092");
        properties.setProperty("bootstrap.servers", kafkaBootstrapServers);
        this.consumer = new KafkaConsumer<>(this.properties);
        List<PartitionInfo> partitions = consumer.partitionsFor(topic);
        this.consumer.close();

        this.consumer = new KafkaConsumer<>(this.properties);
        partitions.forEach(System.out::println);
        TopicPartition topicPartition = new TopicPartition(topic, shardId);
        System.out.println("start assign");
        this.consumer.assign(Collections.singletonList(topicPartition));
        System.out.println("start seek");
        this.consumer.seekToBeginning(Collections.singleton(topicPartition));
        System.out.println("start poll");
        ConsumerRecords<byte[], byte[]> records = consumer.poll(100000);
        System.out.println("poll end");

        try {
            FileWriter fileWriter = new FileWriter("/data/distributed-search/data.txt");
            for (ConsumerRecord<byte[], byte[]> record : records) {
                String key = new String(record.key());
                fileWriter.write(key + "\n");
                String value = new String(record.value());
                fileWriter.write(value + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.consumer.close();
    }

    @Override
    public void startKafkaConsumerFromEnd(int shardId) {

    }

    @Override
    public void stopKafkaConsumer(int shardId) {

    }

    @Override
    public void closeKafkaConsumer(int shardId) {

    }

    @Override
    public void close() {

    }

    @Override
    public JsonNode syncPullTask() {
        return null;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String getTable() {
        return table;
    }

    public static void main(String[] args) {
        String topic = args[0];
        int shardId = Integer.parseInt(args[1]);
        String bootstrapServers = args[2];
        ModulesBuilder modules = ModulesBuilder.getInstance();
        modules.add(new TakeScoopModule());
        TakeScoopSubscriber takeScoopSubscriber = modules.createInjector().getInstance(TakeScoopSubscriber.class);
        takeScoopSubscriber.setKafkaBootstrapServers(bootstrapServers);
        takeScoopSubscriber.setTopic(topic);
        takeScoopSubscriber.startKafkaConsumer(shardId);
        System.out.println("run success");
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    @Override
    public String getIndexDeserializer() {
        return "com.huya.search.facing.convert.TestDataConvert";
    }

    static class TakeScoopModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Settings.class).annotatedWith(Names.named("Kafka-Subscriber")).to(KafkaSubscriberSettings.class).in(Singleton.class);
        }
    }

}
