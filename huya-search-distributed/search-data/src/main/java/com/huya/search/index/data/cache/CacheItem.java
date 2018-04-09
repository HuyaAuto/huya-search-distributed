package com.huya.search.index.data.cache;


import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public interface CacheItem {

    void add(PartDocIdsCache pdc);

    boolean expired();

    void putPointMap(Map<String, Integer> pointMap);

    Map<String,List<Integer>> getCache();
}
