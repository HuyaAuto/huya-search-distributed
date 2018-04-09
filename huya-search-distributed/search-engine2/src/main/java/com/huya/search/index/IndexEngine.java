package com.huya.search.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.index.block.DataBlockManager;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.opeation.*;
import com.huya.search.service.AbstractOrderService;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
@Singleton
public class IndexEngine extends AbstractOrderService implements Engine {

    private Logger LOG = LoggerFactory.getLogger(IndexEngine.class);

    private final DataBlockManager dataBlockManager;

    @Inject
    private IndexEngine(DataBlockManager dataBlockManager) {
        this.dataBlockManager = dataBlockManager;
    }

    @Override
    public void openShards(String table, int shardId) throws ShardsOperatorException {
        dataBlockManager.openShards(OperationFactory.getOpenShardsContext(table, shardId));
    }

    @Override
    public void closeShards(String table, int shardId) throws ShardsOperatorException {
        dataBlockManager.closeShards(OperationFactory.getCloseShardsContext(table, shardId));
    }

    @Override
    public void openShards(OpenContext openContext) throws ShardsOperatorException {
        dataBlockManager.openShards(openContext);
    }

    @Override
    public void closeShards(CloseContext closeContext) throws ShardsOperatorException {
        dataBlockManager.closeShards(closeContext);
    }

    @Override
    public void put(String table, int id, long offset, String timeStamp, List<SearchDataItem> items) throws ShardsOperatorException {
        dataBlockManager.insert(OperationFactory.getInsertContext(table, id, offset, timeStamp, items));
    }

    @Override
    public void put(String table, SearchDataRow searchDataRow) throws ShardsOperatorException {
        dataBlockManager.insert(OperationFactory.getInsertContext(table, searchDataRow));
    }

    @Override
    public JsonNode insertStat() {
        return dataBlockManager.insertStat();
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext shardsQueryContext) throws IOException, ExecutionException, InterruptedException {
        return dataBlockManager.query(shardsQueryContext);
    }

    @Override
    public void refresh(String table) {
        dataBlockManager.refresh(OperationFactory.getRefreshContext(table));
    }

    @Override
    public void refresh(RefreshContext refreshContext) {
        dataBlockManager.refresh(refreshContext);
    }

    @Override
    public boolean exist(String table, int shardId, long offset, long unixTime) throws ShardsOperatorException {
        return dataBlockManager.exist(OperationFactory.getExistContext(table, shardId, offset, unixTime));
    }

    @Override
    public boolean exist(ExistContext existContext) throws ShardsOperatorException {
        return dataBlockManager.exist(existContext);
    }

    @Override
    public long lastPartitionMaxOffset(String table, int shardId) throws ShardsOperatorException {
        return dataBlockManager.lastPartitionMaxOffset(OperationFactory.getRestoreContext(table, shardId));
    }

    @Override
    public long lastPartitionMaxOffset(RestoreContext restoreContext) throws ShardsOperatorException {
        return dataBlockManager.lastPartitionMaxOffset(restoreContext);
    }

    @Override
    public long maxOffset(String table, int shardId, long unixTime) throws ShardsOperatorException {
        return dataBlockManager.maxOffset(OperationFactory.getPartitionRestore(table, shardId, unixTime));
    }

    @Override
    public long maxOffset(PartitionRestoreContext partitionRestoreContext) throws ShardsOperatorException {
        return dataBlockManager.maxOffset(partitionRestoreContext);
    }

    @Override
    public void check() {
        dataBlockManager.check();
    }


    @Override
    public void refresh(String table, String timestamp) {
        dataBlockManager.refresh(OperationFactory.getRefreshContext(table, timestamp));
    }

    @Override
    protected void doStart() throws SearchException {
        dataBlockManager.setPriority(priority() + 1);
        dataBlockManager.start();
        LOG.info(getName() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        dataBlockManager.stop();
        LOG.info(getName() + " stopped");
    }

    @Override
    protected void doClose() throws SearchException {
        dataBlockManager.close();
        LOG.info(getName() + " closed");
    }

    @Override
    public String getName() {
        return "IndexEngine";
    }


}
