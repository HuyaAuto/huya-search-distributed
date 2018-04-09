package com.huya.search.index.dsl.where.expression.timestamp;

public class YYYY_MM_DD_HH_mmExplain extends UnixTimeRangeExplain {

    public YYYY_MM_DD_HH_mmExplain(String value) {
        super(value);
    }

    @Override
    public long range() {
        return 60000;
    }
}
