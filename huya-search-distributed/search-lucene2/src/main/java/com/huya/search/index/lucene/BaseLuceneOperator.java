package com.huya.search.index.lucene;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.CycleCoordinate;
import com.huya.search.index.opeation.CycleCoordinateContext;
import com.huya.search.memory.UseFreq;
import com.huya.search.memory.UseFreqObject;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.TimeSensitive;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/9.
 */
public abstract class BaseLuceneOperator implements TimeSensitive, UseFreq {

    private UseFreq useFreq;

    public BaseLuceneOperator() {
        this.useFreq = UseFreqObject.newInstance();
    }

    public BaseLuceneOperator(UseFreq useFreq) {
        this.useFreq = useFreq;
    }

    public abstract void tryClose() throws IOException, InterruptedException;

    public abstract void close() throws IOException;

    public abstract void rollbackClose() throws IOException;

    public abstract boolean isClose();

    @Override
    public void addWriteFreq() {
        useFreq.addWriteFreq();
    }

    @Override
    public void addReadFreq() {
        useFreq.addReadFreq();
    }

    @Override
    public void addRefreshFreq() {
        useFreq.addRefreshFreq();
    }

    @Override
    public int refreshTotal() {
        return useFreq.refreshTotal();
    }

    @Override
    public double refreshFreq() {
        return useFreq.refreshFreq();
    }

    @Override
    public long refreshIdleTime() {
        return useFreq.refreshIdleTime();
    }

    @Override
    public void clear() {
        useFreq.clear();
    }

    @Override
    public int writeTotal() {
        return useFreq.writeTotal();
    }

    @Override
    public double writeFreq() {
        return useFreq.writeFreq();
    }

    @Override
    public int readTotal() {
        return useFreq.readTotal();
    }

    @Override
    public double readFreq() {
        return useFreq.readFreq();
    }

    @Override
    public double freq() {
        return useFreq.freq();
    }

    @Override
    public int total() {
        return useFreq.total();
    }

    @Override
    public long runTotalTime() {
        return useFreq.runTotalTime();
    }

    @Override
    public long idleTime() {
        return useFreq.idleTime();
    }

    @Override
    public long writeIdleTime() {
        return useFreq.writeIdleTime();
    }

    @Override
    public long readIdleTime() {
        return useFreq.readIdleTime();
    }

    public UseFreq getUseFreq() {
        return useFreq;
    }

    public void setUseFreq(UseFreq useFreq) {
        this.useFreq = useFreq;
    }

    public abstract long writeRamUsed();

    public String tag() {
        return getShardMetaDefine().path();
    }

    public PartitionCycle getPartitionCycle() {
        return getShardMetaDefine().getPartitionCycle();
    }

    public abstract ShardMetaDefine getShardMetaDefine();

    @Override
    public JsonNode report() {
        JsonNode jsonNode = useFreq.report();
        assert jsonNode instanceof ObjectNode;
        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put("writeRamUsed", writeRamUsed());
        objectNode.put("luceneOperator", tag());
        return objectNode;
    }

    public CycleCoordinate getCycleCoordinate() {
        ShardMetaDefine shardMetaDefine = getShardMetaDefine();
        String table = shardMetaDefine.getTable();
        int shardId = shardMetaDefine.getShardId();
        long cycle = shardMetaDefine.getPartitionCycle().getFloor();
        return new CycleCoordinateContext(table, shardId, cycle);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Object o) {
        return useFreq.compareTo(o);
    }


    public abstract boolean luceneOperatorEquals(Object o);

    public abstract int luceneOperatorHashCode();

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        return luceneOperatorEquals(o);
    }

    @Override
    public int hashCode() {
        return luceneOperatorHashCode();
    }
}
