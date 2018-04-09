package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class LongMinListener extends DefaultAggrFunUnit<Long> {

    public static LongMinListener newInstance(String field) {
        return new LongMinListener(field);
    }

    private long number = Long.MAX_VALUE;

    private String field;

    private LongMinListener(String field) {
        this.field = field;
    }

    @Override
    public AggrFun getType() {
        return AggrFun.MIN;
    }

    @Override
    public void add(IndexableField field) {
        Number numberValue = field.numericValue();
        if (numberValue != null) {
            number = Math.min(number, numberValue.longValue());
        }
        else {
            number = Math.min(number, Long.parseLong(field.stringValue()));
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
        number = Long.MAX_VALUE;
    }

    @Override
    public LongMinListener clone() {
        return newInstance(field);
    }
}
