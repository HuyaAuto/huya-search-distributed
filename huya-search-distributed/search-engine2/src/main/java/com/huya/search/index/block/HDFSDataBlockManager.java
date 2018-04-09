package com.huya.search.index.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.*;
import com.huya.search.memory.MemoryControl;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
@Singleton
public class HDFSDataBlockManager extends DataBlockManager {

    private Logger LOG = LoggerFactory.getLogger(HDFSDataBlockManager.class);

    private DataBlocks dataBlocks;

    private MemoryControl memoryControl;

    @Inject
    public HDFSDataBlockManager(DataBlocks dataBlocks, MemoryControl memoryControl) {
        this.dataBlocks = dataBlocks;
        this.memoryControl = memoryControl;
    }

    @Override
    public void openShards(OpenContext openContext) throws ShardsOperatorException {
        Partitions partitions = dataBlocks.getPartitions(openContext);
        partitions.openShard(openContext);
    }

    @Override
    public void closeShards(CloseContext closeContext) throws ShardsOperatorException {
        Partitions partitions = dataBlocks.getPartitions(closeContext);
        partitions.closeShard(closeContext);
    }

    @Override
    public void insert(InsertContext context) throws ShardsOperatorException {
        Partitions partitions = dataBlocks.getPartitions(context);
        partitions.insert(context);
    }

    @Override
    public JsonNode insertStat() {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        for (Partitions partitions : dataBlocks) {
            objectNode.set(partitions.getTable(), partitions.insertStat());
        }
        return objectNode;
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext context) throws InterruptedException, ExecutionException, IOException {
        Partitions partitions = dataBlocks.getPartitions(context);
        return partitions.query(context);
    }

    @Override
    public void refresh(RefreshContext context) {
        if (context.isRefreshAllTable()) {
            dataBlocks.refresh(context);
        }
        else {
            Partitions partitions = dataBlocks.getPartitions(context);
            partitions.refresh(context);
        }
    }

    @Override
    public long lastPartitionMaxOffset(RestoreContext restoreContext) throws ShardsOperatorException {
        Partitions partitions = dataBlocks.getPartitions(restoreContext);
        return partitions.lastPartitionMaxOffset(restoreContext);
    }

    @Override
    public long maxOffset(PartitionRestoreContext partitionRestoreContext) throws ShardsOperatorException {
        Partitions partitions = dataBlocks.getPartitions(partitionRestoreContext);
        return partitions.maxOffset(partitionRestoreContext);
    }

    @Override
    public boolean exist(ExistContext existContext) throws ShardsOperatorException {
        Partitions partitions = dataBlocks.getPartitions(existContext);
        return partitions.exist(existContext);
    }

    @Override
    protected void doStart() throws SearchException {
        dataBlocks.setPriority(priority() + 1);
        dataBlocks.start();
        memoryControl.start();
        LOG.info(getName() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        dataBlocks.stop();
        LOG.info(getName() + " stoped");
    }

    @Override
    protected void doClose() throws SearchException {
        memoryControl.close();
        dataBlocks.close();
        LOG.info(getName() + " closed");
    }

    @Override
    public String getName() {
        return "HdfsDataBlockManager";
    }

    @Override
    public boolean check() {
        return dataBlocks.check();
    }
}
