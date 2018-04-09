package com.huya.search.index.data;

import com.huya.search.index.data.merger.Merger;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class AggrNodeMerger extends AbstractNodeMerger {

    public static AggrNodeMerger newInstance(Merger merger) {
        return new AggrNodeMerger(merger);
    }

    AggrNodeMerger(Merger merger) {
        super(merger);
    }


}
