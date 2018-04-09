package com.huya.search.partition;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/4.
 */
public interface TimeSensitive {

    void triggerTimeSensitiveEvent(PartitionCycle cycle) throws IOException;

    static long nextHour() {
        long currentUnixTime = System.currentTimeMillis();
        PartitionCycle cycle = new PartitionCycle(currentUnixTime, PartitionGrain.HOUR);
        return cycle.getCeil() - currentUnixTime;
    }

    static void main(String[] args) {
        System.out.println(nextHour());
    }
}
