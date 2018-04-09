package com.huya.search.index.data;

import com.huya.search.rpc.RpcResult;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.concurrent.Future;
/**
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public interface ShardMerger {

    RpcResult merger(List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList);



}
