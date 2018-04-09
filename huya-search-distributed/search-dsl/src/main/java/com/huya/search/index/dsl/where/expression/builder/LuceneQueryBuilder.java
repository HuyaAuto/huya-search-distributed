package com.huya.search.index.dsl.where.expression.builder;

import com.huya.search.index.dsl.where.expression.ExpressionOperator;
import com.huya.search.index.meta.IndexFeatureType;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public abstract class LuceneQueryBuilder<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public Query build(String field, ExpressionOperator operator, String value, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException {
        T t = tranType(value);
        switch (operator) {
            case EQUAL: return equalBuild(field, t);
            case NOT:   return notBuild(field, t);
            case LT:    return ltBuild(field, t);
            case LTOE:  return ltoeBuild(field, t);
            case MT:    return mtBuild(field, t);
            case MTOE:  return mtoeBuild(field, t);
            case LIKE:  return likeBuild(field, t, indexFeatureType);
        }
        throw new BuildLuceneQueryException(this.getClass().getSimpleName() + " forbidden " + operator + " operator");
    }

    abstract T tranType(String str);

    abstract Query mtoeBuild(String field, T t) throws BuildLuceneQueryException;

    abstract Query mtBuild(String field, T t) throws BuildLuceneQueryException;

    abstract Query ltoeBuild(String field, T t) throws BuildLuceneQueryException;

    abstract Query ltBuild(String field, T t) throws BuildLuceneQueryException;

    abstract Query notBuild(String field, T t) throws BuildLuceneQueryException;

    abstract Query equalBuild(String field, T t) throws BuildLuceneQueryException;

    abstract Query likeBuild(String field, T t, IndexFeatureType indexFeatureType) throws BuildLuceneQueryException;
}
