package com.huya.search.memory;

import com.huya.search.index.meta.ShardMetaDefine;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/22.
 */
public interface MemoryCore<T> {

    void add(T t, ShardMetaDefine metaDefine);

    T get(ShardMetaDefine metaDefine);

    T remove(ShardMetaDefine metaDefine);

    void strategy();

    void close();
}
