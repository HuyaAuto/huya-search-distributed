package com.huya.search.index.data;

import com.huya.search.index.data.merger.Merger;
import com.huya.search.rpc.RpcResult;
import com.huya.search.rpc.RpcResultRow;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/26.
 */
public class AbstractLimitNodeMerger extends AbstractNodeMerger {

    private int limit;

    protected AbstractLimitNodeMerger(Merger merger, int limit) {
        super(merger);
        this.limit = limit;
    }

    @Override
    public RpcResult merger(List<Future<RpcResult>> futures) {
        List<RpcResultRow> list = super.merger(futures).getResult();

        return list.size() > limit
                ? new RpcResult(super.merger(futures).getResult().subList(0, limit))
                : new RpcResult(list);
    }
}
