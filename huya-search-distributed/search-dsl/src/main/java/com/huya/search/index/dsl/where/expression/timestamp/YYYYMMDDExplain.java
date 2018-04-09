package com.huya.search.index.dsl.where.expression.timestamp;

public class YYYYMMDDExplain extends UnixTimeRangeExplain {

    public YYYYMMDDExplain(String value) {
        super(value);
    }

    @Override
    public long range() {
        return 24 * 60 * 60000;
    }
}
