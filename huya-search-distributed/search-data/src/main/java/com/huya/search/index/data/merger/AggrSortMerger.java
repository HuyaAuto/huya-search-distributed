package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.impl.PartitionDocs;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class AggrSortMerger extends AggrMerger {

    private SortMerger sortMerger;

    public AggrSortMerger(DefaultAggrFunUnitSet defaultAggrFunUnitSet, SortMerger sortMerger) {
        super(defaultAggrFunUnitSet);
        this.sortMerger = sortMerger;
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> result() {
        QueryResult<? extends Iterable<IndexableField>> queryResult =  super.result();
        sortMerger.add(PartitionDocs.newInstance(String.valueOf(""), queryResult.getCollection()));
        return sortMerger.result();
    }
}
