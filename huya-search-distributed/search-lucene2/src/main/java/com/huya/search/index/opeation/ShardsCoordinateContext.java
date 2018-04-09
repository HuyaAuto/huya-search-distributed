package com.huya.search.index.opeation;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/9.
 */
public class ShardsCoordinateContext implements ShardsCoordinate {

    private String table;

    private List<Integer> shardIds;

    protected ShardsCoordinateContext(String table, List<Integer> shardIds) {
        this.table = table;
        this.shardIds = shardIds;
    }
    @Override
    public List<Integer> getShardIds() {
        return shardIds;
    }

    @Override
    public String getTable() {
        return table;
    }
}
