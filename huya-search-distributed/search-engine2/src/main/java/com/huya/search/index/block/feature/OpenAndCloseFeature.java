package com.huya.search.index.block.feature;

import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.opeation.CloseContext;
import com.huya.search.index.opeation.OpenContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/19.
 */
public interface OpenAndCloseFeature {

    void openShards(OpenContext openContext) throws ShardsOperatorException;

    void closeShards(CloseContext closeContext) throws ShardsOperatorException;

}
