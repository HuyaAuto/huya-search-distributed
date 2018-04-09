package com.huya.search.index.data;

import com.huya.search.index.data.merger.Merger;
import com.huya.search.rpc.RpcResult;
import com.huya.search.rpc.RpcResultRow;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/26.
 */
public class AbstractLimitShardMerger extends AbstractShardMerger {

    private int limit;

    protected AbstractLimitShardMerger(Merger merger, int limitSize) {
        super(merger);
        this.limit = limitSize;
    }

    @Override
    public RpcResult merger(List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList) {
        List<RpcResultRow> list = super.merger(queryResultFutureList).getResult();

        return list.size() > limit
                ? new RpcResult(list.subList(0, limit))
                : new RpcResult(list);
    }
}
