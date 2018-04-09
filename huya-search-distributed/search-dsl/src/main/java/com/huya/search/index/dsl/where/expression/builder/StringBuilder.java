package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IntactMetaDefine;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class StringBuilder extends LuceneQueryBuilder<String> {

    private IntactMetaDefine intactMetaDefine;

    public static StringBuilder create(IntactMetaDefine intactMetaDefine) {
        return new StringBuilder(intactMetaDefine);
    }

    private StringBuilder(IntactMetaDefine intactMetaDefine) {
        this.intactMetaDefine = intactMetaDefine;
    }

    @Override
    String tranType(String str) {
        return str;
    }

    @Override
    Query mtoeBuild(String field, String s) throws BuildLuceneQueryException {
        return new TermRangeQuery(field, new BytesRef(s), null, true, false);
    }

    @Override
    Query mtBuild(String field, String s) throws BuildLuceneQueryException {
        return new TermRangeQuery(field, new BytesRef(s), null, false, false);
    }

    @Override
    Query ltoeBuild(String field, String s) throws BuildLuceneQueryException {
        return new TermRangeQuery(field, null, new BytesRef(s), false, true);
    }

    @Override
    Query ltBuild(String field, String s) throws BuildLuceneQueryException {
        return new TermRangeQuery(field, null, new BytesRef(s), false, false);
    }

    @Override
    Query notBuild(String field, String s) throws BuildLuceneQueryException {
        Query query = new TermQuery(new Term(field, s));
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST_NOT);
        return bqb.build();
    }

    @Override
    Query equalBuild(String field, String s) throws BuildLuceneQueryException {
        return new TermQuery(new Term(field, s));
    }

    @Override
    Query likeBuild(String field, String s, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
        return new TermQuery(new Term(field, s));
    }

    public IntactMetaDefine getIntactMetaDefine() {
        return intactMetaDefine;
    }
}
