package com.huya.search.index.data.merger;

import com.huya.search.index.data.merger.aggr.*;
import com.huya.search.index.data.function.AggrFun;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class DefaultAggrFunSetBuilder extends AggrFunSetBuilder {


    public static DefaultAggrFunSetBuilder newInstance() {
        return new DefaultAggrFunSetBuilder();
    }


    protected DefaultAggrFunUnit getStringUnit(String field, AggrFun aggrFun) {
        switch (aggrFun) {
            case COUNT: return CountListener.newInstance(field);
            case MAX: return StringMaxListener.newInstance(field);
            case MIN: return StringMinListener.newInstance(field);
            default: return CountListener.newInstance(field);
        }    }

    protected DefaultAggrFunUnit getDoubleUnit(String field, AggrFun aggrFun) {
        switch (aggrFun) {
            case COUNT: return CountListener.newInstance(field);
            case MAX: return DoubleMaxListener.newInstance(field);
            case MIN: return DoubleMinListener.newInstance(field);
            case SUM: return DoubleSumListener.newInstance(field);
            default: return CountListener.newInstance(field);
        }
    }

    protected DefaultAggrFunUnit getLongUnit(String field, AggrFun aggrFun) {
        switch (aggrFun) {
            case COUNT: return CountListener.newInstance(field);
            case MAX: return LongMaxListener.newInstance(field);
            case MIN: return LongMinListener.newInstance(field);
            case SUM: return LongSumListener.newInstance(field);
            default: return CountListener.newInstance(field);
        }
    }
}
