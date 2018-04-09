package com.huya.search.index.meta;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public class MetaException extends RuntimeException {

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
