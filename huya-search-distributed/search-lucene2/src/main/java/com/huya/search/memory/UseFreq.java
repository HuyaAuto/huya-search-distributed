package com.huya.search.memory;

import com.fasterxml.jackson.databind.JsonNode;

public interface UseFreq extends Comparable {

    void addWriteFreq();

    void addReadFreq();

    void addRefreshFreq();

    void clear();

    int writeTotal();

    double writeFreq();

    int readTotal();

    double readFreq();

    int refreshTotal();

    double refreshFreq();

    double freq();

    int total();

    long runTotalTime();

    long idleTime();

    long writeIdleTime();

    long readIdleTime();

    long refreshIdleTime();

    JsonNode report();
}
