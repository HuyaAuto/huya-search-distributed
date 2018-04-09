package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IntactMetaDefine;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class TextBuilder extends LuceneQueryBuilder<String> {

    private StringBuilder stringBuilder;

    public static TextBuilder create(IntactMetaDefine intactMetaDefine) {
        return new TextBuilder(intactMetaDefine);
    }

    private TextBuilder(IntactMetaDefine intactMetaDefine) {
        this.stringBuilder = StringBuilder.create(intactMetaDefine);
    }

    @Override
    String tranType(String str) {
        return stringBuilder.tranType(str);
    }

    @Override
    Query mtoeBuild(String field, String s) throws BuildLuceneQueryException {
        return stringBuilder.mtoeBuild(field, s);
    }

    @Override
    Query mtBuild(String field, String s) throws BuildLuceneQueryException {
        return stringBuilder.mtoeBuild(field, s);
    }

    @Override
    Query ltoeBuild(String field, String s) throws BuildLuceneQueryException {
        return stringBuilder.ltoeBuild(field, s);
    }

    @Override
    Query ltBuild(String field, String s) throws BuildLuceneQueryException {
        return stringBuilder.ltBuild(field, s);
    }

    @Override
    Query notBuild(String field, String s) throws BuildLuceneQueryException {
        return stringBuilder.notBuild(field, s);
    }

    @Override
    Query equalBuild(String field, String s) throws BuildLuceneQueryException {
        return stringBuilder.equalBuild(field, s);
    }

    @Override
    Query likeBuild(String field, String s, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
//        IntactMetaDefine intactMetaDefine = stringBuilder.getIntactMetaDefine();
//        IndexFeatureType indexFeatureType = intactMetaDefine.getMap().get(field);
        Analyzer analyzer = indexFeatureType.getAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);

        /**
         * 默认启用and， 例如：+content:1 +content:2 +content:3
         * or，          例如：content:1 content:2 content:3
         */
        parser.setDefaultOperator(QueryParser.Operator.OR);
        try {
            Query query = parser.parse(s);
            logger.info("query:" + query.toString());
            return query;
        } catch (ParseException e) {
            throw new BuildLuceneQueryException("parse '" + s + "' error", e);
        }
    }

}
