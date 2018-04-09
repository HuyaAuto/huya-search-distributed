package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/31.
 */
public interface ShardContext {

    long getUnixTime();

    int getShardId();
}
