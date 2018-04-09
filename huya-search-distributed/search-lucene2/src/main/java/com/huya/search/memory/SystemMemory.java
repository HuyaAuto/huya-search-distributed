package com.huya.search.memory;

import com.google.inject.Singleton;
import sun.misc.SharedSecrets;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/18.
 */
@Singleton
public class SystemMemory {

    private static final class SystemMemoryHandle {
        private static final SystemMemory INSTANCE = new SystemMemory();
    }

    public static SystemMemory getInstance() {
        return SystemMemoryHandle.INSTANCE;
    }

    private static final MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();

    private SystemMemory() {}

    public long getHeapMemoryMax() {
        return mBean.getHeapMemoryUsage().getMax();
    }

    public long getHeapMemoryUsage() {
        return mBean.getHeapMemoryUsage().getUsed();
    }

    public long getHeapMemoryFree() {
        return getHeapMemoryMax() - getHeapMemoryFree();
    }

    public int getFreeHeapPercent() {
        return (int) (((float) getHeapMemoryFree() / getHeapMemoryMax()) * 100);
    }

    public long getNonHeapMemoryUsage() {
        return SharedSecrets.getJavaNioAccess().getDirectBufferPool()
                .getMemoryUsed();
    }

    public long getNonHeapMemoryMax() {
        return sun.misc.VM.maxDirectMemory();
    }

    public long getNonHeapMemoryFree() {
        return getNonHeapMemoryMax() - getNonHeapMemoryUsage();
    }

    public int getFreeNonHeapPercent() {
        return (int) (((float) getNonHeapMemoryFree() / getNonHeapMemoryMax()) * 100);
    }

}
