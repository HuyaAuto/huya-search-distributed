package com.huya.search.index.dsl.where.expression.timestamp;

import com.google.common.collect.Range;

import java.util.List;

public interface TimestampExplain {

    Range<Long> equal();

    Range<Long> mt();

    Range<Long> mtoe();

    Range<Long> lt();

    Range<Long> ltoe();

    List<Range<Long>> not();
}
