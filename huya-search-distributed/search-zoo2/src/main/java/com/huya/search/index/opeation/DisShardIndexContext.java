package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/2.
 */
public class DisShardIndexContext extends DisIndexContext {

    private int shardId;

    public int getShardId() {
        return shardId;
    }

    public DisShardIndexContext setShardId(int shardId) {
        this.shardId = shardId;
        return this;
    }
}
