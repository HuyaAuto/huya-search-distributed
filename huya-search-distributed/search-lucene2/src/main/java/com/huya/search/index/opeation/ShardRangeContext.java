package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/1.
 */
public interface ShardRangeContext {

    boolean inRange(long unixTime);

    int getShardId();

}
