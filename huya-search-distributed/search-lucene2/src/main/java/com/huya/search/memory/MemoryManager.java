package com.huya.search.memory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.service.AbstractOrderService;
import com.huya.search.settings.Settings;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/21.
 */
@Singleton
public abstract class MemoryManager<T> extends AbstractOrderService {

    private MemoryCore<T> memoryCore;

    @Inject
    public MemoryManager(Settings settings, MemoryCore<T> memoryCore) {
        super(settings);
        this.memoryCore = memoryCore;
    }

    protected void apply(T t, ShardMetaDefine metaDefine) throws IOException {
        try {
            addMemoryObject(t, metaDefine);
            apply(t);
        } finally {
            notifyApply(metaDefine);
        }
    }

    protected abstract void notifyApply(ShardMetaDefine metaDefine);

    protected abstract void apply(T t) throws IOException;

    protected void free(ShardMetaDefine metaDefine) throws IOException {
        try {
            T t = removeMemoryObject(metaDefine);
            free(t);
        } finally {
            notifyFree(metaDefine);
        }
    }

    protected abstract void notifyFree(ShardMetaDefine metaDefine);


    protected abstract void free(T t) throws IOException;

    protected void addMemoryObject(T t, ShardMetaDefine metaDefine) {
        memoryCore.add(t, metaDefine);
    }

    protected T getMemoryObject(ShardMetaDefine metaDefine) {
        return memoryCore.get(metaDefine);
    }

    protected T removeMemoryObject(ShardMetaDefine metaDefine) {
        return memoryCore.remove(metaDefine);
    }

    protected void closeMemoryObject() {
        memoryCore.close();
    }
}
