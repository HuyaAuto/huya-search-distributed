package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class LongMaxListener extends DefaultAggrFunUnit<Long> {

    public static LongMaxListener newInstance(String field) {
        return new LongMaxListener(field);
    }

    private long number = Long.MIN_VALUE;

    private String field;

    private LongMaxListener(String field) {
        this.field = field;
    }

    @Override
    public AggrFun getType() {
        return AggrFun.MAX;
    }

    @Override
    public void add(IndexableField field) {
        Number numberValue = field.numericValue();
        if (numberValue != null) {
            number = Math.max(number, numberValue.longValue());
        }
        else {
            number = Math.max(number, Long.parseLong(field.stringValue()));
        }
    }

    @Override
    public IndexableField result() {
        return AggrIndexableField.numberIndexableField(field, number);
    }

    @Override
    public Long getValue() {
        return number;
    }

    @Override
    public void clear() {
        number = Long.MIN_VALUE;
    }

    @Override
    public LongMaxListener clone() {
        return newInstance(field);
    }
}
