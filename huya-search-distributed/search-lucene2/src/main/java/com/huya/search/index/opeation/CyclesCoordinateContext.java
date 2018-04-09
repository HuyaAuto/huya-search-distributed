package com.huya.search.index.opeation;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/9.
 */
public class CyclesCoordinateContext extends ShardCoordinateContext implements CyclesCoordinate {

    private List<Long> cycles;

    public CyclesCoordinateContext(String table, int shardId, List<Long> cycles) {
        super(table, shardId);
        this.cycles = cycles;
    }

    public CyclesCoordinateContext(ShardCoordinate shardCoordinate, List<Long> cycles) {
        super(shardCoordinate.getTable(), shardCoordinate.getShardId());
        this.cycles = cycles;
    }

    public List<Long> getCycles() {
        return cycles;
    }
}
