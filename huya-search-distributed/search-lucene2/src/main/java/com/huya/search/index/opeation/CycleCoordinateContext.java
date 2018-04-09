package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/9.
 */
public class CycleCoordinateContext extends ShardCoordinateContext implements CycleCoordinate {

    private long cycle;

    public CycleCoordinateContext(String table, int shardId, long cycle) {
        super(table, shardId);
        this.cycle = cycle;
    }

    public long getCycle() {
        return cycle;
    }
}
