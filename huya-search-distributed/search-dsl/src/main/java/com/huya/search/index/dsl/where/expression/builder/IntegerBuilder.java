package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFeatureType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class IntegerBuilder extends LuceneQueryBuilder<Integer> {

    public static IntegerBuilder create() {
        return new IntegerBuilder();
    }

    private IntegerBuilder() {}

    @Override
    Integer tranType(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Query mtoeBuild(String field, Integer i) {
        return IntPoint.newRangeQuery(field, i, Integer.MAX_VALUE);
    }

    @Override
    public Query mtBuild(String field, Integer i) {
        return mtoeBuild(field, Math.addExact(i, 1));
    }

    @Override
    public Query ltoeBuild(String field, Integer i) {
        return IntPoint.newRangeQuery(field, Integer.MIN_VALUE, i);
    }

    @Override
    public Query ltBuild(String field, Integer i) {
        return ltoeBuild(field, Math.addExact(i, -1));
    }

    @Override
    public Query notBuild(String field, Integer i) {
        Query query = IntPoint.newExactQuery(field, i);
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST_NOT);
        return bqb.build();
    }

    @Override
    public Query equalBuild(String field, Integer i) {
        return IntPoint.newExactQuery(field, i);
    }

    @Override
    public Query likeBuild(String field, Integer integer, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
        throw new BuildLuceneQueryException("integer forbidden like operator");
    }
}
