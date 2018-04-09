package com.huya.search.index.data.cache;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public interface DataCache {

    void add(UUID uuid, CacheItem cacheItem);

    Map<String,List<Integer>> get(UUID uuid);

    void update(UUID uuid, Map<String, Integer> pointMap);
}
