package com.huya.search.index.block;

import com.huya.search.index.block.feature.CheckFeature;
import com.huya.search.index.block.feature.RefreshFeature;
import com.huya.search.index.meta.MetaFollower;
import com.huya.search.index.opeation.ExecutorContext;
import com.huya.search.service.AbstractOrderService;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public abstract class DataBlocks extends AbstractOrderService
        implements MetaFollower, RefreshFeature, CheckFeature, Iterable<Partitions> {

    abstract Partitions getPartitions(ExecutorContext executorContext);

}
