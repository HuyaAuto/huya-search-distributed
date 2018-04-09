package com.huya.search.index.dsl.where.expression.timestamp;

import com.google.common.collect.Range;
import com.huya.search.util.JodaUtil;

import java.util.ArrayList;
import java.util.List;

public class UnixTimePointExplain implements TimestampExplain {

    private String value;

    private long unixTime = -1L;

    public UnixTimePointExplain(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public long getUnixTime() {
        if (unixTime == -1L) unixTime = getParseUnixTime();
        return unixTime;
    }

    public long getParseUnixTime() {
        return JodaUtil.getFormatterByStr(getValue()).parseDateTime(getValue()).getMillis();
    }

    @Override
    public Range<Long> equal() {
        long unixTime = getUnixTime();
        return Range.closed(unixTime, unixTime);
    }

    @Override
    public Range<Long> mt() {
        return Range.greaterThan(getUnixTime());
    }

    @Override
    public Range<Long> mtoe() {
        return Range.atLeast(getUnixTime());
    }

    @Override
    public Range<Long> lt() {
        return Range.lessThan(getUnixTime());
    }

    @Override
    public Range<Long> ltoe() {
        return Range.atMost(getUnixTime());
    }

    @Override
    public List<Range<Long>> not() {
        List<Range<Long>> ranges = new ArrayList<>();
        ranges.add(lt());
        ranges.add(mt());
        return ranges;
    }
}
