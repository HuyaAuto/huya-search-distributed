package com.huya.search.index.block;

import com.huya.search.index.lucene.OperatorPool;
import com.huya.search.index.opeation.ShardCoordinate;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
public interface ShardsFactory {

    Shards create(OperatorPool pool, ShardCoordinate shardCoordinate);
}
