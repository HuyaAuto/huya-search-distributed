package com.huya.search.memory;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/3.
 */
public interface MemoryStrategy {

    void doBetween95And100();

    void doBetween90And95();

    void doBetween80And90();

    void doBelow80();

    void start();

    void close();
}
