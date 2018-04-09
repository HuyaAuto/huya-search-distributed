package com.huya.search.index.data;


import com.huya.search.index.data.merger.Merger;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class AggrShardMerger extends AbstractShardMerger {

    public static AggrShardMerger newInstance(Merger merger) {
        return new AggrShardMerger(merger);
    }

    private AggrShardMerger(Merger merger) {
        super(merger);
    }

}
