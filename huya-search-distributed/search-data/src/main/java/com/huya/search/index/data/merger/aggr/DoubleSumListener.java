package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.index.IndexableField;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class DoubleSumListener extends DefaultAggrFunUnit<Double> {

    public static DoubleSumListener newInstance(String field) {
        return new DoubleSumListener(field);
    }

    private double number = 0;

    private String field;

    private DoubleSumListener(String field) {
        this.field = field;
    }

    @Override
    public AggrFun getType() {
        return AggrFun.SUM;
    }

    @Override
    public void add(IndexableField field) {
        Number numberValue = field.numericValue();
        if (numberValue != null) {
            number += numberValue.doubleValue();
        }
        else {
            number += Double.parseDouble(field.stringValue());
        }
    }

    @Override
    public IndexableField result() {
        return AggrIndexableField.numberIndexableField(field, number);
    }

    @Override
    public Double getValue() {
        return number;
    }

    @Override
    public void clear() {
        number = 0;
    }

    @Override
    public DoubleSumListener clone() {
        return newInstance(field);
    }
}
