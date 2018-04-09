package com.huya.search.index.data.function;

import com.huya.search.index.meta.IndexFieldType;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public enum AggrFun implements Function {

    COUNT("count"),
    SUM("sum"),
    MAX("max"),
    MIN("min");

    private String str;

    AggrFun(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

    public static AggrFun get(String text) {
        switch (text.toLowerCase()) {
            case "count": return COUNT;
            case "sum": return SUM;
            case "max": return MAX;
            case "min": return MIN;
            default: return COUNT;
        }
    }

    public IndexFieldType resultIndexFieldType(List<IndexFieldType> attrType) {
        assert attrType == null || attrType.size() == 1;
        switch (this) {
            case COUNT: return IndexFieldType.Long;
            case MIN:
            case MAX:
            case SUM:
                assert attrType != null;
                IndexFieldType attr = attrType.get(0);
                return attr == IndexFieldType.Integer || attr == IndexFieldType.Long
                    ? IndexFieldType.Long : IndexFieldType.Double;
            default: return IndexFieldType.Long;
        }
    }

    public FunctionType getFunctionType() {
        return FunctionType.AGGR;
    }

    public String functionName() {
        return str;
    }
}
