package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class DoubleMinListener extends DefaultAggrFunUnit<Double> {

    public static DoubleMinListener newInstance(String field) {
        return new DoubleMinListener(field);
    }

    private double number = Double.MAX_VALUE;

    private String field;

    private DoubleMinListener(String field) {
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
            number = Math.min(number, numberValue.doubleValue());
        }
        else {
            number = Math.min(number, Double.parseDouble(field.stringValue()));
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
        number = Double.MAX_VALUE;
    }

    @Override
    public DoubleMinListener clone() {
        return newInstance(field);
    }
}
