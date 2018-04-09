package com.huya.search.index.block;

import com.huya.search.index.block.feature.*;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.meta.CorrespondTable;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.*;
import com.huya.search.partition.TimeSensitive;
import com.huya.search.service.AbstractLifecycleService;
import com.huya.search.settings.Settings;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public abstract class Partitions extends AbstractLifecycleService
        implements CorrespondTable, InsertFeature, ShardQueryFeature, RefreshFeature,
        RestoreFeature, ExistFeature, TimeSensitive, Iterable<Shards> {

    protected static final Logger LOG = LoggerFactory.getLogger(Partitions.class);

    static final ExecutorService es = ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);

    Partitions(Settings settings) {
        super(settings);
    }

    public abstract void initShardsVector();

    @Override
    public void insert(InsertContext insertContext) throws ShardsOperatorException {
//        assert lifecycle.started();
        getShard(insertContext).insert(insertContext);
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext queryContext) throws InterruptedException, ExecutionException, IOException {
        long start = System.currentTimeMillis();
        try {
            return getShards(queryContext).query(queryContext, queryContext.getShardNum(), es);
        } finally {
            long end   = System.currentTimeMillis();
            LOG.info("get range use time : " + (end - start) + " ms");
        }
    }

    public abstract void openShard(OpenContext openContext) throws ShardsOperatorException;

    public abstract void closeShard(CloseContext closeContext) throws ShardsOperatorException;

    @Override
    public String getTable() {
        return getTimelineMetaDefine().getTable();
    }

    abstract Shard getShard(ShardContext shardContext) throws ShardsOperatorException;

    abstract QueryShards getShards(ShardsContext shardsContext) throws InterruptedException, ExecutionException, IOException;

    abstract TimelineMetaDefine getTimelineMetaDefine();

    abstract void check();

}
