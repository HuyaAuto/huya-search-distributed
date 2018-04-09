package com.huya.search.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/3.
 */
public abstract class MemoryMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(MemoryMonitor.class);

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    private volatile MemoryState memoryState = MemoryState.BELOW_80_PERCENT;

    private MemoryStrategy memoryStrategy;

    public MemoryMonitor(MemoryStrategy memoryStrategy) {
        this.memoryStrategy = memoryStrategy;
    }

    public void check() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        double percentage = (double) (totalMemory - freeMemory) / totalMemory;
        if (percentage >= 0.95) {
            if (memoryState != MemoryState.BETWEEN_95_AND_100_PERCENT) {
                memoryState = MemoryState.BETWEEN_95_AND_100_PERCENT;
                memoryStrategy.doBetween95And100();
            }
        }
        else if (percentage >= 0.9) {
            if (memoryState != MemoryState.BETWEEN_90_AND_95_PERCENT) {
                memoryState = MemoryState.BETWEEN_90_AND_95_PERCENT;
                memoryStrategy.doBetween90And95();
            }
        }
        else if (percentage >= 0.8) {
            if (memoryState != MemoryState.BETWEEN_80_AND_90_PERCENT) {
                memoryState = MemoryState.BETWEEN_80_AND_90_PERCENT;
                memoryStrategy.doBetween80And90();
            }
        }
        else {
            if (memoryState != MemoryState.BELOW_80_PERCENT) {
                memoryState = MemoryState.BELOW_80_PERCENT;
                memoryStrategy.doBelow80();
            }
        }
        LOG.info("percentage {} memoryState {}", percentage, memoryState.name());
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this::check, 19, 19, TimeUnit.SECONDS);
    }

    public void close() {
        scheduledExecutorService.shutdown();
    }

}
