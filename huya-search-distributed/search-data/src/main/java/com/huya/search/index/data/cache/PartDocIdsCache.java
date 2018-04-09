package com.huya.search.index.data.cache;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/6.
 */
public interface PartDocIdsCache {

    String tag();

    List<Integer> getDocIds();
}
