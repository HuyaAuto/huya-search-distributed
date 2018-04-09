package com.huya.search.index.block.feature;

import com.huya.search.SearchException;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.DisQueryContext;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public interface DisQueryFeature {

    QueryResult<? extends Iterable<IndexableField>> query(DisQueryContext disQueryContext) throws SearchException;

}
