package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/8.
 */
public class OpenContext extends ShardCoordinateContext {

    public static OpenContext newInstance(String table, int shardId) {
        return new OpenContext(table, shardId);
    }

    private OpenContext(String table, int shardId) {
        super(table, shardId);
    }
}
