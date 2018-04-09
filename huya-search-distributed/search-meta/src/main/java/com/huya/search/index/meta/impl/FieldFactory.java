package com.huya.search.index.meta.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IndexField;
import com.huya.search.index.meta.IndexFieldType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class FieldFactory {

    public static final String ID = "id";

    public static final String OFFSET = "offset";

    public static final String TIME_STAMP = "timestamp";

    public static IndexableField createIndexableField(String name, Object object) {
        if (object instanceof Integer) {
            return new IntPoint(name, (int) object);
        }
        else if (object instanceof Long) {
            return new LongPoint(name, (long) object);
        }
        else if (object instanceof Float) {
            return new FloatPoint(name, (float) object);
        }
        else if (object instanceof Double) {
            return new DoublePoint(name, (double) object);
        }
        else if (object instanceof String) {
            return new StringField(name, (String) object, Field.Store.NO);
        }
        else {
            throw new NoExistIndexTypeException("no exist index type : " + object.getClass().getName());
        }
    }

    public static IndexField createIndexField(String name, Object object, IndexFeatureType indexFeatureType) {
        IndexFieldType indexFieldType = indexFeatureType.getType();
        object = indexFieldType.matchOrConvertType(object);
        boolean sort = indexFeatureType.isSort();
        switch (indexFieldType) {
            case Integer: return buildIntegerField(name, (int) object, sort);
            case Long:
            case Date: return buildLongField(name, (long) object, sort);
            case Float: return buildFloatField(name, (float) object, sort);
            case Double: return buildDoubleField(name, (double) object, sort);
            case String: return buildStringKeyField(name, (String) object, sort);
            case Text: return buildTextFragField(name, (String) object, sort, indexFeatureType.getAnalyzer());
            default: throw new NoExistIndexTypeException("no exist index type : " + indexFieldType.name());
        }
    }

    public static IndexableField createIndexableField(String name, JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return new StringField(name, jsonNode.textValue(), Field.Store.NO);
        }
        else if (jsonNode.isInt()) {
            return new IntPoint(name, jsonNode.intValue());
        }
        else if (jsonNode.isLong()) {
            return new LongPoint(name, jsonNode.longValue());
        }
        else if (jsonNode.isFloat()) {
            return new FloatPoint(name, jsonNode.floatValue());
        }
        else if (jsonNode.isDouble()) {
            return new DoublePoint(name, jsonNode.doubleValue());
        }
        else {
            throw new NoExistIndexTypeException("no exist index type : " + jsonNode.toString());
        }
    }

    public static IndexField createIdIndexField(int id) {
        return buildIntegerField(ID, id, false);
    }

    public static IndexField createOffsetIndexField(long offset) {
        return buildLongField(OFFSET, offset, true);
    }

    public static IndexField createTimestampField(long unixTime) {
        return buildLongField(TIME_STAMP, unixTime, true);
    }

    private static IntegerField buildIntegerField(String name, int value, boolean sort) {
        return new IntegerField(name, value, sort);
    }

    private static LongField buildLongField(String name, long value, boolean sort) {
        return new LongField(name, value, sort);
    }

    private static FloatField buildFloatField(String name, float value, boolean sort) {
        return new FloatField(name, value, sort);
    }

    private static DoubleField buildDoubleField(String name, double value, boolean sort) {
        return new DoubleField(name, value, sort);
    }

    private static StringKeyField buildStringKeyField(String name, String value, boolean sort) {
       return new StringKeyField(name, value, sort);
    }

    private static TextFragField buildTextFragField(String name, String value, boolean sort, Analyzer analyzer) {
        return new TextFragField(name, value, sort, analyzer);
    }

}
