package com.huya.search.index.block.feature;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.ShardsQueryContext;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/31.
 */
public interface ShardQueryFeature {

    QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext queryContext) throws InterruptedException, ExecutionException, IOException;

}
