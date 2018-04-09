package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public interface Executor<T> {

    T run(ExecutorContext context);
}
