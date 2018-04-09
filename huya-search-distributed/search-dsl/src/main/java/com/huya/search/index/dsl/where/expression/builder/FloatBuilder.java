package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IntactMetaDefine;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class FloatBuilder extends LuceneQueryBuilder<Float> {

    private IntactMetaDefine intactMetaDefine;

    public static FloatBuilder create(IntactMetaDefine intactMetaDefine) {
        return new FloatBuilder(intactMetaDefine);
    }
    
    private FloatBuilder(IntactMetaDefine intactMetaDefine) {
        this.intactMetaDefine = intactMetaDefine;
    }

    @Override
    Float tranType(String str) {
        return Float.parseFloat(str);
    }

    @Override
    Query mtoeBuild(String field, Float f) throws BuildLuceneQueryException {
        return FloatPoint.newRangeQuery(field, f, Float.MAX_VALUE);
    }

    @Override
    Query mtBuild(String field, Float f) throws BuildLuceneQueryException {
        f += (float) intactMetaDefine.accuracy();
        return mtoeBuild(field, f);
    }

    @Override
    Query ltoeBuild(String field, Float f) throws BuildLuceneQueryException {
        return FloatPoint.newRangeQuery(field, Float.MIN_VALUE, f);
    }

    @Override
    Query ltBuild(String field, Float f) throws BuildLuceneQueryException {
        f -= (float) intactMetaDefine.accuracy();
        return ltoeBuild(field, f);
    }

    @Override
    Query notBuild(String field, Float f) throws BuildLuceneQueryException {
        Query query = FloatPoint.newExactQuery(field, f);
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST_NOT);
        return bqb.build();
    }

    @Override
    Query equalBuild(String field, Float f) throws BuildLuceneQueryException {
        return FloatPoint.newExactQuery(field, f);
    }

    @Override
    Query likeBuild(String field, Float f, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
        throw new BuildLuceneQueryException("float forbidden like operator");
    }
}
