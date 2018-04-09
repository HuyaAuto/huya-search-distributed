package com.huya.search.index.block;

import org.apache.lucene.store.Directory;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/9.
 */
public interface SearchLockOperator {

    void lock(Directory dir);

    void unLock(Directory dir);
}
