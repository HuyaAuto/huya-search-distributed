package com.huya.search.index.opeation;


/**
 * Created by zhangyiqun1@yy.com on 2017/12/16.
 */
public class ExistContext implements ExecutorContext  {

    private String table;

    private int shardId;

    private long offset;

    private long unixTime;

    @Override
    public String getTable() {
        return table;
    }

    public ExistContext setTable(String table) {
        this.table = table;
        return this;
    }

    public int getShardId() {
        return shardId;
    }

    public ExistContext setShardId(int shardId) {
        this.shardId = shardId;
        return this;
    }

    public long getOffset() {
        return offset;
    }

    public ExistContext setOffset(long offset) {
        this.offset = offset;
        return this;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public ExistContext setUnixTime(long unixTime) {
        this.unixTime = unixTime;
        return this;
    }
}
