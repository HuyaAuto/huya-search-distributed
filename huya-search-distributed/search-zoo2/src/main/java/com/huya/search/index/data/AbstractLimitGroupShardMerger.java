package com.huya.search.index.data;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.rpc.RpcResult;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/26.
 */
public class AbstractLimitGroupShardMerger extends AbstractLimitShardMerger {

    public static AbstractLimitGroupShardMerger newInstance(Merger merger, GroupByItems items, int limitSize) {
        return new AbstractLimitGroupShardMerger(merger, items, limitSize);
    }

    private GroupByItems items;

    protected AbstractLimitGroupShardMerger(Merger merger, GroupByItems items, int limitSize) {
        super(merger, limitSize);
        this.items = items;
    }

    @Override
    public RpcResult merger(List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList) {
        ResultUtil.mergerFutureResult(merger, queryResultFutureList);
        return ResultUtil.tran(merger.result());
    }
}
