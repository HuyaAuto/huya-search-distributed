package com.huya.search.index.opeation;

import com.huya.search.index.data.NodeMerger;
import com.huya.search.index.data.ShardMerger;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public interface DisMergerAble {

    ShardMerger getShardMerger();

    NodeMerger getNodeMerger();
}
