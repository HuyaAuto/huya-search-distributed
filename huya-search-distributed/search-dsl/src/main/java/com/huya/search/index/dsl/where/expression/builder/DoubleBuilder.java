package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IntactMetaDefine;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class DoubleBuilder extends LuceneQueryBuilder<Double> {

    private IntactMetaDefine intactMetaDefine;

    public static DoubleBuilder create(IntactMetaDefine intactMetaDefine) {
        return new DoubleBuilder(intactMetaDefine);
    }

    private DoubleBuilder(IntactMetaDefine intactMetaDefine) {
        this.intactMetaDefine = intactMetaDefine;
    }

    @Override
    Double tranType(String str) {
        return Double.parseDouble(str);
    }

    @Override
    Query mtoeBuild(String field, Double d) throws BuildLuceneQueryException {
        return DoublePoint.newRangeQuery(field, d, Double.MAX_VALUE);
    }

    @Override
    Query mtBuild(String field, Double d) throws BuildLuceneQueryException {
        d += intactMetaDefine.accuracy();
        return mtoeBuild(field, d);
    }

    @Override
    Query ltoeBuild(String field, Double d) throws BuildLuceneQueryException {
        return DoublePoint.newRangeQuery(field, Double.MIN_VALUE, d);
    }

    @Override
    Query ltBuild(String field, Double d) throws BuildLuceneQueryException {
        d -= intactMetaDefine.accuracy();
        return ltoeBuild(field, d);
    }

    @Override
    Query notBuild(String field, Double d) throws BuildLuceneQueryException {
        Query query = DoublePoint.newExactQuery(field, d);
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST_NOT);
        return bqb.build();
    }

    @Override
    Query equalBuild(String field, Double d) throws BuildLuceneQueryException {
        return DoublePoint.newExactQuery(field, d);
    }

    @Override
    Query likeBuild(String field, Double d, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
        throw new BuildLuceneQueryException("double forbidden like operator");
    }
}
