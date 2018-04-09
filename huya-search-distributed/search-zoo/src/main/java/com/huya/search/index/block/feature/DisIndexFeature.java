package com.huya.search.index.block.feature;

import com.huya.search.SearchException;
import com.huya.search.index.opeation.DisIndexContext;
import com.huya.search.index.opeation.DisShardIndexContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public interface DisIndexFeature {

    void insert(DisIndexContext disIndexContext) throws SearchException;

    void stopInsert(DisIndexContext disIndexContext) throws SearchException;

    void insertFromEnd(DisIndexContext disIndexContext) throws SearchException;

    void insertShard(DisShardIndexContext disIndexContext) throws SearchException;

    void stopInsertShard(DisShardIndexContext disIndexContext) throws SearchException;

}
