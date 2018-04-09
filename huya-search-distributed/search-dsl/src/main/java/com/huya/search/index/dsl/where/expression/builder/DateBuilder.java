package com.huya.search.index.dsl.where.expression.builder;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class DateBuilder extends LongBuilder {

    public static DateBuilder create() {
        return new DateBuilder();
    }

    private DateBuilder() {
        super();
    }

    public Query rangeBuild(String field, Range<Long> range) {
        assert range.hasLowerBound() || range.hasUpperBound();
        if (range.hasUpperBound() && range.hasLowerBound()) {
            BooleanQuery.Builder bqb = new BooleanQuery.Builder();
            bqb.add(lowQuery(field, range), BooleanClause.Occur.FILTER);
            bqb.add(upQuery(field, range), BooleanClause.Occur.FILTER);
            return bqb.build();
        }
        else if (range.hasUpperBound()) {
            return upQuery(field, range);
        }
        else {
            return lowQuery(field, range);
        }
    }

    private Query lowQuery(String field, Range<Long> range) {
        BoundType type = range.lowerBoundType();
        long low = range.lowerEndpoint();
        return type == BoundType.CLOSED
                ? mtoeBuild(field, low)
                : mtBuild(field, low);
    }

    private Query upQuery(String field, Range<Long> range) {
        BoundType type = range.upperBoundType();
        long up = range.upperEndpoint();
        return type == BoundType.CLOSED
                ? ltoeBuild(field, up)
                : ltBuild(field, up);
    }


    public Query rangesBuild(String field, List<Range<Long>> rangeList) {
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        for (Range<Long> range : rangeList) {
            bqb.add(rangeBuild(field, range), BooleanClause.Occur.SHOULD);
        }
        return bqb.build();
    }
}
