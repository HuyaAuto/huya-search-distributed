package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFeatureType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class LongBuilder extends LuceneQueryBuilder<Long> {

    public static LongBuilder create() {
        return new LongBuilder();
    }

    LongBuilder() {}

    @Override
    Long tranType(String str) {
        return Long.parseLong(str);
    }

    @Override
    public Query mtoeBuild(String field, Long l) throws BuildLuceneQueryException {
        return LongPoint.newRangeQuery(field, l, Long.MAX_VALUE);
    }

    @Override
    public Query mtBuild(String field, Long l) throws BuildLuceneQueryException {
        return mtoeBuild(field, Math.addExact(l, 1));
    }

    @Override
    public Query ltoeBuild(String field, Long l) throws BuildLuceneQueryException {
        return LongPoint.newRangeQuery(field, Long.MIN_VALUE, l);
    }

    @Override
    public Query ltBuild(String field, Long l) throws BuildLuceneQueryException {
        return ltoeBuild(field, Math.addExact(l, -1));
    }

    @Override
    public Query notBuild(String field, Long l) throws BuildLuceneQueryException {
        Query query = LongPoint.newExactQuery(field, l);
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST_NOT);
        return bqb.build();
    }

    @Override
    public Query equalBuild(String field, Long l) throws BuildLuceneQueryException {
        return LongPoint.newExactQuery(field, l);
    }

    @Override
    public Query likeBuild(String field, Long l, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
        throw new BuildLuceneQueryException("long forbidden like operator");
    }


}
