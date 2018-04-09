package com.huya.search.memory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.block.DataBlocks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/5.
 */
@Singleton
public class DefaultMemoryStrategy implements MemoryStrategy {

    private static final ExecutorService es = Executors.newSingleThreadExecutor();

    private DataBlocks dataBlocks;

    private Runnable between95And100;

    private Runnable between90And95;

    private Runnable between80And90;

    private Runnable below80;

    private volatile Runnable currentMemoryStrategy;

    @Inject
    public DefaultMemoryStrategy(DataBlocks dataBlocks) {
        this.dataBlocks = dataBlocks;
    }

    @Override
    public void doBetween95And100() {
        if (currentMemoryStrategy != between95And100) {
            currentMemoryStrategy = between95And100;
            es.submit(currentMemoryStrategy);
        }
    }

    @Override
    public void doBetween90And95() {
        if (currentMemoryStrategy != between90And95) {
            currentMemoryStrategy = between90And95;
            es.submit(currentMemoryStrategy);
        }
    }

    @Override
    public void doBetween80And90() {
        if (currentMemoryStrategy != between80And90) {
            currentMemoryStrategy = between80And90;
            es.submit(currentMemoryStrategy);
        }
    }

    @Override
    public void doBelow80() {
        if (currentMemoryStrategy != below80) {
            currentMemoryStrategy = below80;
            es.submit(currentMemoryStrategy);
        }
    }

    @Override
    public void start() {
        initStrategy();
        es.submit(currentMemoryStrategy);
    }

    private void initStrategy() {
        below80 = getBelow80();
        between80And90 = getBetween80And90();
        between90And95 = getBetween90And95();
        between95And100 = getBetween95And100();
        currentMemoryStrategy = below80;
    }

    @Override
    public void close() {

    }

    private void check(long ideTime, Runnable runnable) {
        while (true) {
            dataBlocks.forEach(partition -> partition.forEach(shards -> shards.forEach(shard -> {
                if (shard.check(ideTime)) {
                    shards.remove(shard);
                }
            })));
            try {
                if (currentMemoryStrategy != runnable) break;
                TimeUnit.SECONDS.sleep(60);
                if (currentMemoryStrategy != runnable) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private Runnable getBelow80() {
        long ideTime = 12 * 60 * 60 * 1000;
        return () -> check(ideTime, below80);
    }

    private Runnable getBetween80And90() {
        long ideTime = 6 * 60 * 60 * 1000;
        return () -> check(ideTime, between80And90);
    }

    private Runnable getBetween90And95() {
        long ideTime = 3 * 60 * 60 * 1000;
        return () -> check(ideTime, between80And90);
    }

    private Runnable getBetween95And100() {
        long ideTime = 60 * 60 * 1000;
        return () -> check(ideTime, between80And90);
    }
}
