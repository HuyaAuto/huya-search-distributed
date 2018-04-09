package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class DoubleMaxListener extends DefaultAggrFunUnit<Double> {

    public static DoubleMaxListener newInstance(String field) {
        return new DoubleMaxListener(field);
    }

    private double number = Double.MIN_VALUE;

    private String field;

    private DoubleMaxListener(String field) {
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
            number = Math.max(number, numberValue.doubleValue());
        }
        else {
            number = Math.max(number, Double.parseDouble(field.stringValue()));
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
        number = Double.MIN_VALUE;
    }


    @Override
    public DoubleMaxListener clone() {
        return newInstance(field);
    }

}
