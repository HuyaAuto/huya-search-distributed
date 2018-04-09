package com.huya.search.index.dsl.parse;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public class ParseExpection extends RuntimeException {

    public ParseExpection(String message) {
        super(message);
    }

    public ParseExpection(String message, Exception e) {
        super(message, e);
    }
}
