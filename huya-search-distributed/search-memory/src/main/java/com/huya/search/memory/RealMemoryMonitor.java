package com.huya.search.memory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/3.
 */
@Singleton
public class RealMemoryMonitor extends MemoryMonitor {

    @Inject
    public RealMemoryMonitor(MemoryStrategy memoryStrategy) {
        super(memoryStrategy);
    }
}