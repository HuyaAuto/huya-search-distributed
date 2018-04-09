package com.huya.search.index.block;

import com.huya.search.index.block.feature.CheckFeature;
import com.huya.search.index.block.feature.InsertFeature;
import com.huya.search.index.block.feature.RefreshFeature;
import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.meta.CorrespondTable;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.CloseContext;
import com.huya.search.partition.TimeSensitive;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/30.
 */
public interface Shard extends CorrespondTable, InsertFeature, RefreshFeature, CheckFeature, TimeSensitive, LazyLuceneOperator {

    long maxOffset() throws ShardsOperatorException;

    ShardMetaDefine getMetaDefine();

    int getShardId();

    boolean hit(CloseContext closeContext);

    boolean exist(long offset) throws ShardsOperatorException;

    boolean check(long ideTime);
}
