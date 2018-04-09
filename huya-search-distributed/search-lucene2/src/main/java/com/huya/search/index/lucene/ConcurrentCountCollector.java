package com.huya.search.index.lucene;

import com.google.common.collect.Range;
import com.huya.search.index.data.SingleQueryResult;
import com.huya.search.index.meta.MetaEnum;
import com.huya.search.util.JodaUtil;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SimpleCollector;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/21.
 */
public class ConcurrentCountCollector extends SimpleCollector {

    private Query query;

    private long startUnixTime;

    private long endUnixTime = -1L;

    private Query mergerQuery = null;

    private AtomicInteger integer = new AtomicInteger(0);

    public ConcurrentCountCollector(Query query, Range<Long> range) {
        this.query = query;

        // 精确查询、根据数据区间，调用Lucene API
        switch (range.lowerBoundType()) {
            case OPEN:
                this.startUnixTime = Math.addExact(range.lowerEndpoint(), -1);
                break;
                default:
                    this.startUnixTime = range.lowerEndpoint();
                    break;
        }

        switch (range.upperBoundType()) {
            case OPEN:
                this.endUnixTime = Math.addExact(range.upperEndpoint(), -1);
                break;
            default:
                this.endUnixTime = range.upperEndpoint();
                break;
        }
    }

    @Override
    public void collect(int doc) throws IOException {
        integer.addAndGet(1);
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    public Query getQuery() {
        if (mergerQuery == null) {
            Query unixTimeQuery;
            if (endUnixTime == -1) {
                unixTimeQuery = LongPoint.newRangeQuery(MetaEnum.TIMESTAMP, startUnixTime, Long.MAX_VALUE);
            } else {
                unixTimeQuery = LongPoint.newRangeQuery(MetaEnum.TIMESTAMP, startUnixTime, endUnixTime);
            }
            BooleanQuery.Builder bqb = new BooleanQuery.Builder();
            bqb.add(unixTimeQuery, BooleanClause.Occur.FILTER);
            bqb.add(query, BooleanClause.Occur.FILTER);
            mergerQuery = bqb.build();
        }
        return mergerQuery;
    }

    public Iterable<IndexableField> getResult() {
        String timestamp = new DateTime(startUnixTime).toString(JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER);
        IndexableField timestampIndexableField = SingleQueryResult.createStringIndexableField("timestamp", timestamp);
        IndexableField countIndexableField = SingleQueryResult.createNumberIndexableField("*", integer.get());
        return Arrays.asList(timestampIndexableField, countIndexableField);
    }
}
