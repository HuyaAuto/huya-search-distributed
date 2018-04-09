package com.huya.search.index.block;

import com.huya.search.index.lucene.WriterAndReadLuceneOperator;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/31.
 */
public class ShardFactory {

    public static IndexShard create(WriterAndReadLuceneOperator luceneOperator, int shardId) {
        return new IndexShard(luceneOperator, shardId);
    }
}
