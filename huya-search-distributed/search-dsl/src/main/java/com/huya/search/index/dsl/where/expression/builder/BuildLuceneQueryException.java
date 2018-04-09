package com.huya.search.index.dsl.where.expression.builder;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/29.
 */
public class BuildLuceneQueryException extends RuntimeException {

    public BuildLuceneQueryException(String message) {
        super(message);
    }

    public BuildLuceneQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
