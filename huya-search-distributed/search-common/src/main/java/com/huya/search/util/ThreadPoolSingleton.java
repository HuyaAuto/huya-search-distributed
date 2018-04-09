package com.huya.search.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * 重构线程池、各个模块的线程不再混在一起
 *
 * @Refactor ZhangXueJun
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public class ThreadPoolSingleton {

    private static final class ThreadPoolSingletonHandle {
        private static final ThreadPoolSingleton INSTANCE = new ThreadPoolSingleton();
    }

    public static ThreadPoolSingleton getInstance() {
        return ThreadPoolSingletonHandle.INSTANCE;
    }

    private static final HashMap<String, ForkJoinPool> forkJoinPools = Maps.newHashMap();
    private static final Map<String, ExecutorService> executors = Maps.newHashMap();

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        String grainPoolScale = System.getProperty("grain.pool.scale");
        int grainPoolNum = (int) (availableProcessors * 0.75);
        if (StringUtils.isNotEmpty(grainPoolScale)) {
            try {
                grainPoolNum = (int) (Double.valueOf(grainPoolScale) * availableProcessors);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ForkJoinPool genericForkJoinPool = ForkJoinPool.commonPool();
        ForkJoinPool grainForkJoinPool = new ForkJoinPool(grainPoolNum);

        forkJoinPools.put(Names.GENERIC, genericForkJoinPool);
        forkJoinPools.put(Names.GRAIN, grainForkJoinPool);

        executors.put(Names.GENERIC, Executors.newCachedThreadPool());
        executors.put(Names.GRAIN, Executors.newFixedThreadPool(grainPoolNum));
    }

    private ThreadPoolSingleton() {
    }

    public ExecutorService getExecutorService(String poolName) {
        ExecutorService executor = executors.get(poolName);
        if (executor == null) {
            throw new IllegalArgumentException("no executor found for [" + poolName + "]");
        }
        return executor;
    }

    /**
     * 简单拆分forkjoinpool
     */
    public static class Names {
        public static final String GENERIC = "generic";
        public static final String GRAIN = "grain";
    }

    /**
     * 线程池种类
     */
    public static enum ThreadPoolType {
        /**
         * 固定、处理聚合查询
         */
        FIXED("fixed"),

        /**
         * 可动态扩展
         */
        SCALING("scaling");

        private final String type;

        public String getType() {
            return type;
        }

        ThreadPoolType(String type) {
            this.type = type;
        }

        private static final Map<String, ThreadPoolType> TYPE_MAP;

        static {
            Map<String, ThreadPoolType> typeMap = new HashMap<>();
            for (ThreadPoolType threadPoolType : ThreadPoolType.values()) {
                typeMap.put(threadPoolType.getType(), threadPoolType);
            }
            TYPE_MAP = Collections.unmodifiableMap(typeMap);
        }

        public static ThreadPoolType fromType(String type) {
            ThreadPoolType threadPoolType = TYPE_MAP.get(type);
            if (threadPoolType == null) {
                throw new IllegalArgumentException("no ThreadPoolType for " + type);
            }
            return threadPoolType;
        }
    }

    public ForkJoinPool getForkJoinPool(String name) {
        ForkJoinPool forkJoinPool = forkJoinPools.get(name);
        if (forkJoinPool == null) {
            throw new IllegalArgumentException("no forkjoinpool found for [" + name + "]");
        }
        return forkJoinPool;
    }

    public void shutdown() {
        for (ExecutorService executorService : executors.values()) {
            executorService.shutdownNow();
        }
        for (ForkJoinPool forkJoinPool : forkJoinPools.values()) {
            forkJoinPool.shutdownNow();
        }
    }
}
