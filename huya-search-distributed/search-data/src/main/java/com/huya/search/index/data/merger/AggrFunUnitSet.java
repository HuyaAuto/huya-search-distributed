package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import org.apache.lucene.index.IndexableField;


/**
 * Created by zhangyiqun1@yy.com on 2018/3/3.
 */
public interface AggrFunUnitSet {

    QueryResult<Iterable<IndexableField>> result();

}
