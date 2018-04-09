package com.huya.search.index.dsl.where.expression.timestamp;

import com.google.common.collect.Range;
import com.huya.search.util.JodaUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class UnixTimeRangeExplain implements TimestampExplain {

    private String value;

    private long floorUnixTime = -1;

    public UnixTimeRangeExplain(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public long getCeilUnixTime() {
        return getFloorUnixTime() + range();
    }

    public long getFloorUnixTime() {
        if (floorUnixTime == -1) floorUnixTime = getParseFloorUnixTime();
        return floorUnixTime;
    }

    public long getParseFloorUnixTime() {
        return JodaUtil.getFormatterByStr(getValue()).parseDateTime(getValue()).getMillis();
    }

    public abstract long range();

    @Override
    public Range<Long> equal() {
        return Range.closed(getFloorUnixTime(), getCeilUnixTime() - 1);
    }

    @Override
    public Range<Long> mt() {
        return Range.atLeast(getCeilUnixTime());
    }

    @Override
    public Range<Long> mtoe() {
        return Range.atLeast(getFloorUnixTime());
    }

    @Override
    public Range<Long> lt() {
        return Range.lessThan(getFloorUnixTime());
    }

    @Override
    public Range<Long> ltoe() {
        return Range.atMost(getCeilUnixTime());
    }

    @Override
    public List<Range<Long>> not() {
        List<Range<Long>> ranges = new ArrayList<>();
        ranges.add(lt());
        ranges.add(mt());
        return ranges;
    }
}
