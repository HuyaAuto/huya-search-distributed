package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/8.
 */
public class ShardCoordinateContext implements ExecutorContext, ShardCoordinate {

    private String table;

    private int shardId;

    public ShardCoordinateContext(String table, int shardId) {
        this.table = table;
        this.shardId = shardId;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public int getShardId() {
        return shardId;
    }
}
