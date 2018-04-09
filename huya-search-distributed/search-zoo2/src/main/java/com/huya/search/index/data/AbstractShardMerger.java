package com.huya.search.index.data;

import com.huya.search.index.data.merger.Merger;
import com.huya.search.rpc.RpcResult;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class AbstractShardMerger implements ShardMerger {

    protected Merger merger;

    protected AbstractShardMerger(Merger merger) {
        this.merger = merger;
    }

    @Override
    public RpcResult merger(List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList) {
        ResultUtil.mergerFutureResult(merger, queryResultFutureList);
        return ResultUtil.tran(merger.result());
    }

}
