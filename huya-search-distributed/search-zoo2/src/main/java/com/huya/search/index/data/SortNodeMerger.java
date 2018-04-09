package com.huya.search.index.data;

import com.huya.search.index.data.merger.Merger;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public class SortNodeMerger extends AbstractNodeMerger {

    public static SortNodeMerger newInstance(Merger merger) {
        return new SortNodeMerger(merger);
    }

    private SortNodeMerger(Merger merger) {
        super(merger);
    }

}
