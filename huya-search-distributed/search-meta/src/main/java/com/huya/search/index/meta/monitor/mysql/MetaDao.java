package com.huya.search.index.meta.monitor.mysql;

import com.huya.search.index.meta.TimelineMetaDefine;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO rename TableMeta
 *
 * Created by zhangyiqun1@yy.com on 2017/10/20.
 */
public class MetaDao {

    private String table;
    private String topic;
    private String kafkaBootstrapServers;
    private String indexDeserializer;
    private double accuracy;

    private List<ColumnInfo> list;

    public String getTable() {
        return table;
    }

    public MetaDao setTable(String table) {
        this.table = table;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public MetaDao setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public MetaDao setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        return this;
    }

    public String getIndexDeserializer() {
        return indexDeserializer;
    }

    public MetaDao setIndexDeserializer(String indexDeserializer) {
        this.indexDeserializer = indexDeserializer;
        return this;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public MetaDao setAccuracy(double accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public List<ColumnInfo> getList() {
        return list;
    }

    public MetaDao setList(List<ColumnInfo> list) {
        this.list = list;
        return this;
    }

    public static MetaDao newInstance(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();

        MetaDao metaDao = new MetaDao()
                .setTable(table)
                .setTopic(metaDefine.getTopic())
                .setKafkaBootstrapServers(metaDefine.getKafkaBootstrapServers())
                .setIndexDeserializer(metaDefine.getIndexDeserializer())
                .setAccuracy(metaDefine.accuracy());

        List<ColumnInfo> list = new ArrayList<>();

        metaDefine.getTreeMap().values().forEach(
                m -> list.add(ColumnInfo.newInstance(table, m))
        );
        return metaDao.setList(list);
    }
}
