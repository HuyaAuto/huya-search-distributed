package com.huya.search.index.meta.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.*;
import com.huya.search.index.meta.monitor.mysql.MetaDao;
import com.huya.search.util.JsonUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/19.
 */
public class JsonMetaUtil {

    public static MetaDefine createMetaDefineFromBytes(byte[] bytes) {
        return createMetaDefineFromStr(new String(bytes));
    }

    public static MetaDefine createMetaDefineFromStr(String str) {
        assert str != null;
        try {
            return createMetaDefineFromJson((ObjectNode) JsonUtil.getObjectMapper().readTree(str));
        } catch (IOException e) {
            throw new MetaOperatorException("read str to create meta define error", e);
        }
    }

    public static MetaDefine createMetaDefineFromJson(ObjectNode objectNode) {
        MetaDefineBuilder metaDefineBuilder = MetaDefineBuilder.newInstance();
        Map.Entry<String,JsonNode> entry = objectNode.fields().next();
        return metaDefineBuilder.build(IndexMapBuilder.newInstance().add((ObjectNode) entry.getValue()), entry.getKey());
    }


    public static TimelineMetaDefine createTimeMetaDefineFromBytes(byte[] bytes) {
        return createTimelineMetaDefineFromStr(new String(bytes));
    }

    public static TimelineMetaDefine createTimelineMetaDefineFromStr(String str) {
        assert str != null;
        try {
            return createTimelineMetaDefineFromJson((ObjectNode) JsonUtil.getObjectMapper().readTree(str));
        } catch (IOException e) {
            throw new MetaOperatorException("read str to create timeline meta define error", e);
        }
    }

    public static TimelineMetaDefine createTimelineMetaDefineFromJson(ObjectNode objectNode) {
        TimelineMetaDefineBuilder tmBuilder = TimelineMetaDefineBuilder.newInstance();
        MetaDefineBuilder mBuilder  = MetaDefineBuilder.newInstance();

        MetaHeadInfoBuilder metaHeadInfoBuilder = MetaHeadInfoBuilder.newInstance();

        String table                 = objectNode.remove(MetaEnum.TABLE).textValue();
        String topic                 = objectNode.remove(MetaEnum.TOPIC).textValue();
        String kafkaBootstrapServers = objectNode.remove(MetaEnum.KAFKA_BOOTSTRAP_SERVERS).textValue();
        String indexDeserializer     = objectNode.remove(MetaEnum.INDEX_DESERIALIZER).textValue();
        double accuracy              = objectNode.remove(MetaEnum.ACCURACY).doubleValue();

        metaHeadInfoBuilder.setTable(table)
                .setTopic(topic)
                .setKafkaBootstrapServers(kafkaBootstrapServers)
                .setIndexDeserializer(indexDeserializer)
                .setAccuracy(accuracy);

        tmBuilder.addMetaHeadInfo(metaHeadInfoBuilder.build());

        objectNode.fields().forEachRemaining(entry -> {
            String timestampKey = entry.getKey();
            JsonNode jsonNode = entry.getValue();
            MetaDefine metaDefine = mBuilder.build(IndexMapBuilder.newInstance().add((ObjectNode) jsonNode), timestampKey);
            tmBuilder.addMetaDefine(metaDefine);
        });
        return tmBuilder.build();
    }

    public static TimelineMetaDefine createTimelineMetaDefineFromMetaDao(MetaDao metaDao) {
        TimelineMetaDefineBuilder tmBuilder = TimelineMetaDefineBuilder.newInstance();
        MetaDefineBuilder mBuilder  = MetaDefineBuilder.newInstance();

        MetaHeadInfoBuilder metaHeadInfoBuilder = MetaHeadInfoBuilder.newInstance();

        String table                 = metaDao.getTable();
        String topic                 = metaDao.getTopic();
        String kafkaBootstrapServers = metaDao.getKafkaBootstrapServers();
        String indexDeserializer     = metaDao.getIndexDeserializer();
        double accuracy              = metaDao.getAccuracy();

        metaHeadInfoBuilder.setTable(table)
                .setTopic(topic)
                .setKafkaBootstrapServers(kafkaBootstrapServers)
                .setIndexDeserializer(indexDeserializer)
                .setAccuracy(accuracy);

        tmBuilder.addMetaHeadInfo(metaHeadInfoBuilder.build());

        metaDao.getList().forEach(columnInfo -> {
            String partitionName = columnInfo.getPartitionName();
            try {
                JsonNode jsonNode = JsonUtil.getObjectMapper().readTree(columnInfo.getColumnInfo());
                MetaDefine metaDefine = mBuilder.build(IndexMapBuilder.newInstance().add((ObjectNode) jsonNode), partitionName);
                tmBuilder.addMetaDefine(metaDefine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return tmBuilder.build();
    }

}
