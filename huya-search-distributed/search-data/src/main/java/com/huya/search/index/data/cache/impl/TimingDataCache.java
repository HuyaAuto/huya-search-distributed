package com.huya.search.index.data.cache.impl;

import com.huya.search.index.data.cache.CacheItem;
import com.huya.search.index.data.cache.DataCache;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public class TimingDataCache implements DataCache {

    private Map<UUID, CacheItem> cacheItemMap = new ConcurrentHashMap<>();

    @Override
    public void add(UUID uuid, CacheItem cacheItem) {
        cacheItemMap.put(uuid, cacheItem);
    }

    @Override
    public Map<String, List<Integer>> get(UUID uuid) {
        return cacheItemMap.get(uuid).getCache();
    }

    @Override
    public void update(UUID uuid, Map<String, Integer> pointMap) {
        CacheItem cacheItem = cacheItemMap.get(uuid);
        if (cacheItem != null) {
            cacheItem.putPointMap(pointMap);
        }
    }
}
