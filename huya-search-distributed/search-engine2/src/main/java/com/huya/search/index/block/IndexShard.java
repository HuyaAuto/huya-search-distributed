package com.huya.search.index.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.index.lucene.WriterAndReadLuceneOperator;
import com.huya.search.index.lucene.WriterIsCloseException;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.*;
import com.huya.search.partition.PartitionCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/30.
 */
public class IndexShard implements Shard {

    public static final Logger LOG = LoggerFactory.getLogger(IndexShard.class);

    //todo 将配置改成动态
    private static final long MAX_IDE_TIME = 24 * 60 * 60 * 1000;

    private WriterAndReadLuceneOperator luceneOperator;

    private ShardMetaDefine shardMetaDefine;

    private int shardId;

    IndexShard(WriterAndReadLuceneOperator luceneOperator, int shardId) {
        this.luceneOperator = luceneOperator;
        this.shardMetaDefine = luceneOperator.getShardMetaDefine();
        this.shardId = shardId;
    }

    @Override
    public void insert(InsertContext insertContext) throws ShardsOperatorException {
        try {
            luceneOperator.write(insertContext);
        } catch (IOException e) {
            CycleCoordinate cycleCoordinate = luceneOperator.getCycleCoordinate();
            if (e instanceof WriterIsCloseException) {
                throw ShardsOperatorException.luceneWriterIsClose(cycleCoordinate);
            }
            else {
                throw ShardsOperatorException.addDocumentIOException(cycleCoordinate);
            }
        }
    }

    @Override
    public JsonNode insertStat() {
        return luceneOperator.report();
    }

    @Override
    public void refresh(RefreshContext refreshContext) {
        boolean forced = refreshContext.isForced();
        if (!luceneOperator.isClose()) {
            try {
                luceneOperator.refresh(forced);
            } catch (IOException e) {
                String path = luceneOperator.getShardMetaDefine().path();
                LOG.error("path {} refresh error", path, e);
            }
        }
    }

    @Override
    public void triggerTimeSensitiveEvent(PartitionCycle cycle) throws IOException {
        luceneOperator.triggerTimeSensitiveEvent(cycle);
    }

    @Override
    public String getTable() {
        return luceneOperator.getShardMetaDefine().getTable();
    }


    @Override
    public long maxOffset() throws ShardsOperatorException {
        try {
            return luceneOperator.maxOffset();
        } catch (IOException e) {
            LOG.error(String.format("maxOffset query in %s error", this), e);
            throw ShardsOperatorException.queryIOException(luceneOperator.getCycleCoordinate());
        }
    }

    @Override
    public ShardMetaDefine getMetaDefine() {
        return shardMetaDefine;
    }

    @Override
    public int getShardId() {
        return shardId;
    }

    @Override
    public boolean hit(CloseContext closeContext) {
        return shardId == closeContext.getShardId() && Objects.equals(getTable(), closeContext.getTable());
    }

    @Override
    public boolean exist(long offset) throws ShardsOperatorException {
        try {
            return luceneOperator.exist(offset);
        } catch (IOException e) {
            LOG.error(String.format("query offset %d exist in %s", offset, this), e);
            throw ShardsOperatorException.queryIOException(luceneOperator.getCycleCoordinate());
        }
    }

    @Override
    public WriterAndReadLuceneOperator lazyGet() {
        return luceneOperator;
    }

    @Override
    public boolean check() {
        return check(MAX_IDE_TIME);
    }

    @Override
    public boolean check(long ideTime) {
        long currentUnixTime = System.currentTimeMillis();
        long activeTime = currentUnixTime - luceneOperator.getPartitionCycle().getCeil();

        if (luceneOperator.isClose()) {
            String path = luceneOperator.getShardMetaDefine().path();
            LOG.info("luceneOperator already close, so try free {}, insert {} times before", path, luceneOperator.writeTotal());
            return true;
        }
        else if (activeTime >= ideTime) {
            String path = luceneOperator.getShardMetaDefine().path();
            LOG.info("ide time more {}, try free {}, insert {} times", ideTime, path, luceneOperator.writeTotal());
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "IndexShard{" +
                "table=" + getTable() +
                "shardId=" + shardId +
                "cycle=" + luceneOperator.getPartitionCycle().partitionName() +
                '}';
    }

}
