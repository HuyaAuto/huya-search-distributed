package com.huya.search.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.opeation.*;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public interface Engine {

    void openShards(String table, int shardId) throws ShardsOperatorException;

    void closeShards(String table, int shardId) throws ShardsOperatorException;

    void openShards(OpenContext openContext) throws ShardsOperatorException;

    void closeShards(CloseContext closeContext) throws ShardsOperatorException;

    void put(String table, int id, long offset, String timestamp, List<SearchDataItem> items) throws ShardsOperatorException;

    void put(String table, SearchDataRow searchDataRow) throws ShardsOperatorException;

    JsonNode insertStat();

    QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext shardsQueryContext) throws IOException, ExecutionException, InterruptedException;

    void refresh(String table, String timestamp);

    void refresh(String table);

    void refresh(RefreshContext refreshContext);

    boolean exist(String table, int shardId, long offset, long unixTime) throws ShardsOperatorException;

    boolean exist(ExistContext existContext) throws ShardsOperatorException;

    long lastPartitionMaxOffset(String table, int shardId) throws ShardsOperatorException;

    long lastPartitionMaxOffset(RestoreContext restoreContext) throws ShardsOperatorException;

    long maxOffset(String table, int shardId, long unixTime) throws ShardsOperatorException;

    long maxOffset(PartitionRestoreContext partitionRestoreContext) throws ShardsOperatorException;

    void check();
}
