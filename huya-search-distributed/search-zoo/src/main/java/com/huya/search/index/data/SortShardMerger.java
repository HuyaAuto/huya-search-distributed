package com.huya.search.index.data;

import com.huya.search.index.data.merger.Merger;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public class SortShardMerger extends AbstractShardMerger {

    public static SortShardMerger newInstance(Merger merger, int limitSize) {
//        return new SortShardMerger(merger, limitSize);
        return null;
    }

    private SortShardMerger(Merger merger, int limitSize) {
        super(merger);
    }

}
