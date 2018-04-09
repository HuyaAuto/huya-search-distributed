package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.partition.PartitionGrain;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/8.
 */
public class JsonMetaHandle implements MetaHandle {

    private Map<String, String> jsonMap = new HashMap<>();

    public String getJson(String table) {
        return jsonMap.get(table);
    }

    public void putJson(String table, String json) {
        jsonMap.put(table, json);
    }

    @Override
    public MetaCollection load() {
        Map<String, TimelineMetaDefine> map = new ConcurrentHashMap<>();
        jsonMap.forEach((key, value) -> {
            TimelineMetaDefine timelineMetaDefine = JsonMetaUtil.createTimelineMetaDefineFromStr(value);
            String table = timelineMetaDefine.getTable();
            map.put(table, timelineMetaDefine);
        });


        return RealMetaCollection.newInstance(map);
    }

    @Override
    public void unLoad() {
        //do nothing
    }

    @Override
    public TimelineMetaDefine createTimelineMetaDefine(ObjectNode objectNode) {
        return JsonMetaUtil.createTimelineMetaDefineFromJson(objectNode);
    }

    @Override
    public MetaDefine createMetaDefine(TimelineMetaDefine metaDefine, ObjectNode objectNode) {
        MetaDefineBuilder mBuilder = MetaDefineBuilder.newInstance();

        PartitionGrain grain = metaDefine.getLast().getFirstPartitionCycle().getGrain();

        IndexMapBuilder indexMapBuilder = IndexMapBuilder.newInstance().add(objectNode);

        return mBuilder.build(indexMapBuilder, grain, new DateTime().getMillis());
    }

    @Override
    public void persistence(TimelineMetaDefine metaDefine) {
        ObjectNode objectNode = metaDefine.toObject();
        objectNode.remove(MetaEnum.ID);
        objectNode.remove(MetaEnum.TIMESTAMP);
        String table = objectNode.get(MetaEnum.TABLE).textValue();
        jsonMap.put(table, objectNode.toString());
    }

    @Override
    public void remove(String table) {
        jsonMap.remove(table);
    }

    @Override
    public void update(String table, MetaDefine metaDefine) {
        throw new MetaOperatorException("json meta handle is not support update metaDefine");

    }
}
