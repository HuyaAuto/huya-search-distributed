package com.huya.search.index.opeation;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/7.
 */
public class RestoreContext implements ExecutorContext {

    private String table;

    private int shardId;

    @Override
    public String getTable() {
        return table;
    }

    public RestoreContext setTable(String table) {
        this.table = table;
        return this;
    }

    public int getShardId() {
        return shardId;
    }

    public RestoreContext setShardId(int shardId) {
        this.shardId = shardId;
        return this;
    }
}
