package com.huya.search.memory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/3.
 */
@Singleton
public class RealMemoryControlService extends MemoryControl {

    private MemoryMonitor memoryMonitor;

    @Inject
    public RealMemoryControlService(MemoryMonitor memoryMonitor) {
        this.memoryMonitor = memoryMonitor;
    }

    @Override
    protected void doStart() throws SearchException {
        memoryMonitor.start();
    }

    @Override
    protected void doStop() throws SearchException {

    }

    @Override
    protected void doClose() throws SearchException {
        memoryMonitor.close();
    }

    @Override
    public String getName() {
        return "RealMemoryControlService";
    }

}
