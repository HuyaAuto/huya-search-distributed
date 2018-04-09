package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/19.
 */
public class CloseContext extends ShardCoordinateContext {

    public static CloseContext newInstance(String table, int shardId) {
        return new CloseContext(table, shardId);
    }

    private CloseContext(String table, int shardId) {
        super(table, shardId);
    }
}
