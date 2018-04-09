package com.huya.search.memory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.util.JsonUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class UseFreqObject implements UseFreq {

    private AtomicInteger writeFreq = new AtomicInteger();

    private AtomicInteger readFreq = new AtomicInteger();

    private AtomicInteger refreshFreq = new AtomicInteger();

    private long createTimeMillis = System.currentTimeMillis();

    private long lastWriteTimeMillis = -1L;

    private long lastReadTimeMillis = -1L;

    private long lastRefreshTimeMillis = -1L;

    public static UseFreqObject newInstance() {
        return new UseFreqObject();
    }

    private UseFreqObject() {}

    @Override
    public void addWriteFreq() {
        writeFreq.addAndGet(1);
        lastWriteTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void addReadFreq() {
        readFreq.addAndGet(1);
        lastReadTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void addRefreshFreq() {
        refreshFreq.addAndGet(1);
        lastRefreshTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void clear() {
        writeFreq.set(0);
        readFreq.set(0);
        refreshFreq.set(0);
    }

    @Override
    public int writeTotal() {
        return writeFreq.get();
    }

    @Override
    public double writeFreq() {
        return writeFreq.get() / runTotalTime();
    }

    @Override
    public int readTotal() {
        return readFreq.get();
    }

    @Override
    public double readFreq() {
        return readFreq.get() / runTotalTime();
    }

    @Override
    public int refreshTotal() {
        return refreshFreq.get();
    }

    @Override
    public double refreshFreq() {
        return refreshFreq.get() / runTotalTime();
    }

    @Override
    public double freq() {
        return total() / runTotalTime();
    }

    @Override
    public int total() {
        return writeFreq.get() + readFreq.get();
    }

    @Override
    public long runTotalTime() {
        return System.currentTimeMillis() - createTimeMillis;
    }

    @Override
    public long idleTime() {
        return System.currentTimeMillis() - Math.max(getLastWriteTimeMillis(), getLastReadTimeMillis());
    }

    @Override
    public long writeIdleTime() {
        return System.currentTimeMillis() - getLastWriteTimeMillis();
    }

    @Override
    public long readIdleTime() {
        return System.currentTimeMillis() - getLastReadTimeMillis();
    }

    @Override
    public long refreshIdleTime() {
        return System.currentTimeMillis() - getLastRefreshTimeMillis();
    }

    @Override
    public JsonNode report() {
        int    writeTotal      = writeFreq.get();
        int    readTotal       = readFreq.get();
        int    refreshTotal    = refreshFreq.get();
        long   runTotalTime    = runTotalTime();
        int    total           = writeTotal + readTotal;
        long   writeIdleTime   = writeIdleTime();
        long   readIdleTime    = readIdleTime();
        long   refreshIdleTime = refreshIdleTime();

        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        objectNode.put("writeTotal", writeTotal);
        objectNode.put("readTotal", readTotal);
        objectNode.put("refreshTotal", refreshTotal);
        objectNode.put("total", total);
        objectNode.put("runTotalTime", runTotalTime);
        objectNode.put("writeIdleTime", writeIdleTime);
        objectNode.put("readIdleTime", readIdleTime);
        objectNode.put("refreshIdleTime", refreshIdleTime);
        return objectNode;
    }

    private long getLastWriteTimeMillis() {
        return lastWriteTimeMillis == -1L ? createTimeMillis : lastWriteTimeMillis;
    }

    private long getLastReadTimeMillis() {
        return lastReadTimeMillis == -1L ? createTimeMillis : lastReadTimeMillis;
    }

    private long getLastRefreshTimeMillis() {
        return lastRefreshTimeMillis == -1L ? createTimeMillis : lastRefreshTimeMillis;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(freq(), ((UseFreq)o).freq());
    }
}
