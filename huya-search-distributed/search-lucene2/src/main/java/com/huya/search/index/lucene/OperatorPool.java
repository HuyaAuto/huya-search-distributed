package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.ShardCoordinate;
import com.huya.search.partition.PartitionCycle;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/4.
 */
public interface OperatorPool {

    WriterAndReadLuceneOperator getOperator(PartitionCycle cycle, int shardId) throws IOException, ExecutionException, InterruptedException;

    WriterAndReadLuceneOperator getOperator(long unixTime, int shardId) throws IOException, ExecutionException, InterruptedException;

    void removeOperator(ShardMetaDefine metaDefine) throws IOException, InterruptedException;

    List<WriterAndReadLuceneOperator> getLastOperators(ShardCoordinate shardCoordinate, int num) throws ExecutionException, InterruptedException;

    void lockShards(ShardCoordinate shardCoordinate) throws IOException;

    void unLockShards(ShardCoordinate shardCoordinate) throws IOException;
}
