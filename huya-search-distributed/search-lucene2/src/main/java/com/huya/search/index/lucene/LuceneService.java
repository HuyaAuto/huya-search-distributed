package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.ShardCoordinate;
import com.huya.search.memory.MemoryCore;
import com.huya.search.memory.MemoryManager;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.settings.Settings;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/10.
 */
@Singleton
public abstract class LuceneService extends MemoryManager<WriterAndReadLuceneOperator> {

    @Inject
    LuceneService(Settings settings, MemoryCore<WriterAndReadLuceneOperator> memoryCore) {
        super(settings, memoryCore);
    }

    protected abstract WriterAndReadLuceneOperator getOperator(ShardMetaDefine metaDefine) throws ExecutionException, InterruptedException;

//    protected abstract Future<BaseLuceneOperator> getFutureOperator(ShardMetaDefine metaDefine);

    public abstract void removeOperator(ShardMetaDefine metaDefine) throws IOException, InterruptedException;

    public abstract OperatorPool getOperatorPool(TimelineMetaDefine metaDefine);

    public abstract List<PartitionCycle> getLastPartitionCycle(ShardCoordinate shardCoordinate) throws IOException;

    public abstract void lockShards(ShardCoordinate shardCoordinate) throws IOException;

    public abstract void unLockShards(ShardCoordinate shardCoordinate) throws IOException;
}
