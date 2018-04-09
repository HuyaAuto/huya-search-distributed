package com.huya.search.index.data.merger;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.aggr.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class NodeAggrFunSetBuilder extends AggrFunSetBuilder {


    public static NodeAggrFunSetBuilder newInstance() {
        return new NodeAggrFunSetBuilder();
    }


    protected DefaultAggrFunUnit getStringUnit(String field, AggrFun aggrFun) {
        switch (aggrFun) {
            case COUNT: return LongSumListener.newInstance(field);
            case MAX: return StringMaxListener.newInstance(field);
            case MIN: return StringMinListener.newInstance(field);
            default: return LongSumListener.newInstance(field);
        }
    }

    protected DefaultAggrFunUnit getDoubleUnit(String field, AggrFun aggrFun) {
        switch (aggrFun) {
            case COUNT: return LongSumListener.newInstance(field);
            case MAX: return DoubleMaxListener.newInstance(field);
            case MIN: return DoubleMinListener.newInstance(field);
            case SUM: return DoubleSumListener.newInstance(field);
            default: return LongSumListener.newInstance(field);
        }
    }

    protected DefaultAggrFunUnit getLongUnit(String field, AggrFun aggrFun) {
        switch (aggrFun) {
            case COUNT: return LongSumListener.newInstance(field);
            case MAX: return LongMaxListener.newInstance(field);
            case MIN: return LongMinListener.newInstance(field);
            case SUM: return LongSumListener.newInstance(field);
            default: return LongSumListener.newInstance(field);
        }
    }

}
