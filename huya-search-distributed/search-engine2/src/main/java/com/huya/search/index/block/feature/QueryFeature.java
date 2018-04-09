package com.huya.search.index.block.feature;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.QueryContext;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public interface QueryFeature {

    QueryResult<? extends Iterable<IndexableField>> query(QueryContext queryContext);

}
