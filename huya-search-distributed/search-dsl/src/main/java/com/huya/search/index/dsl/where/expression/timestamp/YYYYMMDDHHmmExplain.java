package com.huya.search.index.dsl.where.expression.timestamp;

public class YYYYMMDDHHmmExplain extends UnixTimeRangeExplain {

    public YYYYMMDDHHmmExplain(String value) {
        super(value);
    }

    @Override
    public long range() {
        return 60000;
    }
}
