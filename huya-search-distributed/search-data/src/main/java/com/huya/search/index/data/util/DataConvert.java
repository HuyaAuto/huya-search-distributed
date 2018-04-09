package com.huya.search.index.data.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.impl.FieldFactory;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/10.
 */
public class DataConvert {

    public static ObjectNode rowToObject(Iterable<IndexableField> iterable) {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        iterable.forEach((field) -> {
            String fieldStr    = field.stringValue();
            if (fieldStr != null) objectNode.put(field.name(), fieldStr);

            Number fieldNumber = field.numericValue();
            if (fieldNumber != null) {
                if (fieldNumber instanceof Integer) {
                    objectNode.put(field.name(), (int) fieldNumber);
                }
                else if (fieldNumber instanceof Long) {
                    objectNode.put(field.name(), (long) fieldNumber);
                }
                else if (fieldNumber instanceof Float) {
                    objectNode.put(field.name(), (float) fieldNumber);
                }
                else {
                    objectNode.put(field.name(), fieldNumber.doubleValue());
                }
            }
        });
        return objectNode;
    }


    public static Iterable<IndexableField> objectToRow(ObjectNode object) {
        List<IndexableField> list = new ArrayList<>();
        object.fields().forEachRemaining(entry -> {
            String name = entry.getKey();
            JsonNode jsonNode = entry.getValue();
            if (jsonNode.isTextual())
                list.add(FieldFactory.createIndexableField(name, jsonNode.textValue()));
            else if (jsonNode.isInt())
                list.add(FieldFactory.createIndexableField(name, jsonNode.intValue()));
            else if (jsonNode.isLong())
                list.add(FieldFactory.createIndexableField(name, jsonNode.longValue()));
            else if (jsonNode.isFloat())
                list.add(FieldFactory.createIndexableField(name, jsonNode.floatValue()));
            else if (jsonNode.isDouble())
                list.add(FieldFactory.createIndexableField(name, jsonNode.doubleValue()));

        });
        return list;
    }

    public static Map<String, ?> rowToMap(Iterable<IndexableField> iterable) {
        Map<String, Object> map = new HashMap<>();
        iterable.forEach((field) -> {
            String fieldStr    = field.stringValue();
            if (fieldStr != null) map.put(field.name(), fieldStr);

            Number fieldNumber = field.numericValue();
            if (fieldNumber != null) map.put(field.name(), fieldNumber);
        });
        return map;
    }

    public static Iterable<IndexableField> mapToRow(Map<String, ?> map) {
        List<IndexableField> list = new ArrayList<>();
        map.forEach((key, value) -> list.add(FieldFactory.createIndexableField(key, value)));
        return list;
    }

    public static Object jsonNodeNumberToJavaNumber(JsonNode jsonNode) {
        if (jsonNode.isInt()) return jsonNode.intValue();
        else if (jsonNode.isLong()) return jsonNode.longValue();
        else if (jsonNode.isFloat()) return jsonNode.floatValue();
        else return jsonNode.doubleValue();
    }
}
