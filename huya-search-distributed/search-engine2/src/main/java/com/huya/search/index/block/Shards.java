package com.huya.search.index.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.RefreshContext;
import com.huya.search.index.opeation.ShardsQueryContext;
import com.huya.search.partition.PartitionCycle;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/14.
 */
public interface Shards extends Iterable<Shard> {

    boolean isOpen();

    long lastPartitionMaxOffset() throws ShardsOperatorException;

    Shard getShard(long unixTime) throws ShardsOperatorException;

    QueryShards getQueryShards(Collection<Long> unixTimes);

    void check();

    void remove(Shard shard);

    void refresh(RefreshContext refreshContext);

    void triggerTimeSensitiveEvent(PartitionCycle cycle);

    JsonNode insertStat();

    void open() throws ShardsOperatorException;

    void close() throws ShardsOperatorException;

}
