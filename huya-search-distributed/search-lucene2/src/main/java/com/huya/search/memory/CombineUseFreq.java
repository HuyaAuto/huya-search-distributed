package com.huya.search.memory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/7.
 */
public class CombineUseFreq implements UseFreq {

    public static CombineUseFreq newInstance(UseFreq ... useFreqs) {
        return new CombineUseFreq(useFreqs);
    }

    private UseFreq[] useFreqs;

    public CombineUseFreq(UseFreq[] useFreqs) {
        this.useFreqs = useFreqs;
    }

    @Override
    public void addWriteFreq() {
        throw new RuntimeException("no support addWrite for combineUseFreq");
    }

    @Override
    public void addReadFreq() {
        throw new RuntimeException("no support addRead for combineUseFreq");
    }

    @Override
    public void addRefreshFreq() {
        throw new RuntimeException("no support addRead for combineUseFreq");
    }

    @Override
    public void clear() {
        for (UseFreq useFreq : useFreqs) {
            useFreq.clear();
        }
    }

    @Override
    public int writeTotal() {
        int sum = 0;
        for (UseFreq useFreq : useFreqs) {
            sum += useFreq.writeTotal();
        }
        return sum;
    }

    @Override
    public double writeFreq() {
        return writeTotal() / runTotalTime();
    }

    @Override
    public int readTotal() {
        int sum = 0;
        for (UseFreq useFreq : useFreqs) {
            sum += useFreq.readTotal();
        }
        return sum;
    }

    @Override
    public double readFreq() {
        return readTotal() / runTotalTime();
    }

    @Override
    public int refreshTotal() {
        int sum = 0;
        for (UseFreq useFreq : useFreqs) {
            sum += useFreq.refreshTotal();
        }
        return sum;
    }

    @Override
    public double refreshFreq() {
        return refreshTotal() / runTotalTime();
    }

    @Override
    public double freq() {
        return total() / runTotalTime();
    }

    @Override
    public int total() {
        return writeTotal() + readTotal();
    }

    @Override
    public long runTotalTime() {
        return useFreqs[0].runTotalTime();
    }

    @Override
    public long idleTime() {
        long idleTime = Long.MAX_VALUE;
        for (UseFreq useFreq : useFreqs) {
            idleTime = Math.min(idleTime, useFreq.idleTime());
        }
        return idleTime;
    }

    @Override
    public long writeIdleTime() {
        long writeIdleTime = Long.MAX_VALUE;
        for (UseFreq useFreq : useFreqs) {
            writeIdleTime = Math.min(writeIdleTime, useFreq.writeIdleTime());
        }
        return writeIdleTime;
    }

    @Override
    public long readIdleTime() {
        long readIdleTime = Long.MAX_VALUE;
        for (UseFreq useFreq : useFreqs) {
            readIdleTime = Math.min(readIdleTime, useFreq.readIdleTime());
        }
        return readIdleTime;
    }

    @Override
    public long refreshIdleTime() {
        long refreshIdleTime = Long.MAX_VALUE;
        for (UseFreq useFreq : useFreqs) {
            refreshIdleTime = Math.min(refreshIdleTime, useFreq.refreshIdleTime());
        }
        return refreshIdleTime;
    }

    @Override
    public JsonNode report() {
        throw new RuntimeException("no support report for combineUseFreq");
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(freq(), ((UseFreq)o).freq());
    }
}
