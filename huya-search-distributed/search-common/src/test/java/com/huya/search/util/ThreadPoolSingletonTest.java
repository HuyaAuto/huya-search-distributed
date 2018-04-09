package com.huya.search.util;

import org.junit.Test;

import java.util.concurrent.ForkJoinPool;

/**
 * @author ZhangXueJun
 * @date 2018年04月03日
 */
public class ThreadPoolSingletonTest {

    @Test
    public void testForkJoin() {
        ForkJoinPool forkJoinPool = ThreadPoolSingleton.getInstance().getForkJoinPool(ThreadPoolSingleton.Names.GENERIC);
        System.out.println(forkJoinPool.getPoolSize());
    }

}
