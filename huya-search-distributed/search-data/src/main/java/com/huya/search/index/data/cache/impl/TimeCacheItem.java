package com.huya.search.index.data.cache.impl;

import com.huya.search.QuerySettings;
import com.huya.search.index.data.cache.CacheItem;
import com.huya.search.index.data.cache.PartDocIdsCache;
import com.huya.search.inject.ModulesBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public class TimeCacheItem implements CacheItem {

    private static final long DEFAULT_EXPIRED =
            ModulesBuilder.getInstance().createInjector().getInstance(QuerySettings.class).
                    getAsLong("cache.expired", 60000L);

    public static CacheItem newInstance(long expired) {
        return new TimeCacheItem(expired);
    }

    public static CacheItem newInstance() {
        return new TimeCacheItem(DEFAULT_EXPIRED);
    }

    private final long expired;

    private volatile long lastVisit;

    private Map<String, List<Integer>> cache;

    private Map<String, Integer> pointMap;

    private TimeCacheItem(long expired) {
        this.expired = expired;
        this.lastVisit = System.currentTimeMillis();
    }

    private Map<String, PartDocIdsCache> partMap = new HashMap<>();

    @Override
    public void add(PartDocIdsCache pdc) {
        partMap.put(pdc.tag(), pdc);
    }

    @Override
    public boolean expired() {
        long now = System.currentTimeMillis();
        return now - lastVisit >= expired;
    }

    @Override
    public void putPointMap(Map<String, Integer> pointMap) {
        this.pointMap = pointMap;
    }

    @Override
    public Map<String, List<Integer>> getCache() {
        if (cache == null) {
            cache = new HashMap<>();
            for (Map.Entry<String, PartDocIdsCache> entry : partMap.entrySet()) {
                List<Integer> docIds = entry.getValue().getDocIds();
                String tag = entry.getKey();
                int point = pointMap.get(tag);
                cache.put(tag, docIds.subList(point, docIds.size()));
            }
        }
        return cache;
    }

}
