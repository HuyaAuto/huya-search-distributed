package com.huya.search.index.meta;


/**
 * Created by zhangyiqun1@yy.com on 2017/12/12.
 */
public class MetaHeadInfoBuilder {

    public static MetaHeadInfoBuilder newInstance() {
        return new MetaHeadInfoBuilder();
    }

    private MetaHeadInfoBuilder() {}

    private String table;
    private String topic;
    private String kafkaBootstrapServers;
    private String indexDeserializer;
    private double accuracy;

    public String getTable() {
        return table;
    }

    public MetaHeadInfoBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public MetaHeadInfoBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public MetaHeadInfoBuilder setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        return this;
    }

    public String getIndexDeserializer() {
        return indexDeserializer;
    }

    public MetaHeadInfoBuilder setIndexDeserializer(String indexDeserializer) {
        this.indexDeserializer = indexDeserializer;
        return this;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public MetaHeadInfoBuilder setAccuracy(double accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public MetaHeadInfo build() {
        return new MetaHeadInfo() {

            @Override
            public String getIndexDeserializer() {
                return indexDeserializer;
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
            public double accuracy() {
                return accuracy;
            }
        };
    }
}
