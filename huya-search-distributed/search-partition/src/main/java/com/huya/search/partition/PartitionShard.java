package com.huya.search.partition;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
public interface PartitionShard {

    void add(long unixTime, int shardId);

    void remove(long unixTime);

    void remove(long unixTime, int shardId);

    void remove(int shardId);

    boolean contains(int shardId);

    boolean contains(long unixTime);

    boolean contains(long unixTime, int shardId);

    int loadQuantity();

    int loadShardQuantity(int shardId);

    boolean isIdle();

    boolean isIdle(int shardId);
}
