package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/1.
 */
public class PartitionRestoreContext extends RestoreContext {

    private long unixTime;

    public long getUnixTime() {
        return unixTime;
    }

    public PartitionRestoreContext setUnixTime(long unixTime) {
        this.unixTime = unixTime;
        return this;
    }
}
