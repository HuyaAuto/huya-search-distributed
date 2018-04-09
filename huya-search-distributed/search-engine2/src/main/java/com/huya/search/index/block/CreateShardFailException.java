package com.huya.search.index.block;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/6.
 */
public class CreateShardFailException extends RuntimeException {

    public CreateShardFailException(String message) {
        super(message);
    }

    public CreateShardFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateShardFailException(Throwable cause) {
        super(cause);
    }
}
