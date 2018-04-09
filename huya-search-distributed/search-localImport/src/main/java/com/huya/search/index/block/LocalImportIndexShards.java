package com.huya.search.index.block;

import com.huya.search.index.lucene.OperatorPool;
import com.huya.search.index.opeation.ShardCoordinate;

/**
 * 离线导入程序并不去锁【分片列】
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
public class LocalImportIndexShards extends IndexShards {

    private LocalImportIndexShards(OperatorPool pool, ShardCoordinate shardCoordinate) {
        super(pool, shardCoordinate);
    }

    @Override
    protected void lock() throws ShardsOperatorException {
        //do nothing
    }

    @Override
    protected void unLock() throws ShardsOperatorException {
        //do nothing
    }


}
