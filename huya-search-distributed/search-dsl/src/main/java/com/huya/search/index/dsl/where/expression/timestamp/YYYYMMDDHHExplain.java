package com.huya.search.index.dsl.where.expression.timestamp;

public class YYYYMMDDHHExplain extends UnixTimeRangeExplain {

    public YYYYMMDDHHExplain(String value) {
        super(value);
    }

    @Override
    public long range() {
        return 60 * 60000;
    }
}
