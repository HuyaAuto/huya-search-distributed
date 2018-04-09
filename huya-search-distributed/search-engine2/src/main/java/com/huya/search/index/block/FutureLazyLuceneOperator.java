package com.huya.search.index.block;

import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.lucene.WriterAndReadLuceneOperator;

import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public class FutureLazyLuceneOperator implements LazyLuceneOperator {

    public static FutureLazyLuceneOperator newInstance(Future<Shard> shardFuture) {
        return new FutureLazyLuceneOperator(shardFuture);
    }

    private Future<Shard> shardFuture;

    private FutureLazyLuceneOperator(Future<Shard> shardFuture) {
        this.shardFuture = shardFuture;
    }

    @Override
    public WriterAndReadLuceneOperator lazyGet() throws Exception {
        return shardFuture.get().lazyGet();
    }
}
