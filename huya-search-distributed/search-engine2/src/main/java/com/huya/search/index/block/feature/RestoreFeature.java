package com.huya.search.index.block.feature;

import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.opeation.PartitionRestoreContext;
import com.huya.search.index.opeation.RestoreContext;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/7.
 */
public interface RestoreFeature {

    long lastPartitionMaxOffset(RestoreContext restoreContext) throws ShardsOperatorException;

    long maxOffset(PartitionRestoreContext partitionRestoreContext) throws ShardsOperatorException;
}
