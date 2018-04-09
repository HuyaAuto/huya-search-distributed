package com.huya.search.index.block;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.lucene.OperatorPool;
import com.huya.search.index.opeation.ShardCoordinate;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
@Singleton
public class LocalImportIndexShardsFactory implements ShardsFactory {

    @Inject
    public LocalImportIndexShardsFactory() {}

    @Override
    public Shards create(OperatorPool pool, ShardCoordinate shardCoordinate) {
        return LocalImportIndexShards.newInstance(pool, shardCoordinate);
    }
}
