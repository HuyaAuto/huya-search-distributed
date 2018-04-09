package com.huya.search.index.dsl.where.expression.timestamp;

public class YYYY_MM_DD_HHExplain extends UnixTimeRangeExplain {

    public YYYY_MM_DD_HHExplain(String value) {
        super(value);
    }

    @Override
    public long range() {
        return 60 * 60000;
    }
}
