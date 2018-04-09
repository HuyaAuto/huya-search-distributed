package com.huya.search.index.dsl.where.expression.timestamp;

public class YYYY_MM_DDExplain extends UnixTimeRangeExplain {

    public YYYY_MM_DDExplain(String value) {
        super(value);
    }

    @Override
    public long range() {
        return 24 * 60 * 60000;
    }
}
