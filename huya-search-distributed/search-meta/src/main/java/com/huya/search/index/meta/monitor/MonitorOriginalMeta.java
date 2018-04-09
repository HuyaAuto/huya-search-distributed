package com.huya.search.index.meta.monitor;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public abstract class MonitorOriginalMeta extends MonitorMeta {

    /**
     * 加载原始数据
     */
    public void load() {
        iterator().forEachRemaining(
                meta -> getMonitorMap().values().forEach(
                        m -> m.add(meta)
                )
        );
    }

    public abstract void unload();
}
