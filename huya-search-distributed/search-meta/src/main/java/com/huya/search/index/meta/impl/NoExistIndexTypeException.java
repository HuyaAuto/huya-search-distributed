package com.huya.search.index.meta.impl;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class NoExistIndexTypeException extends RuntimeException {
    public NoExistIndexTypeException(String msg) {
        super(msg);
    }
}
