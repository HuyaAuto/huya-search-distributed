package com.huya.search.index.block.feature;

import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.opeation.ExistContext;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/16.
 */
public interface ExistFeature {

    boolean exist(ExistContext existContext) throws ShardsOperatorException;

}
