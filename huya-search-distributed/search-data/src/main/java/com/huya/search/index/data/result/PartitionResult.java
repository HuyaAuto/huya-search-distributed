package com.huya.search.index.data.result;

import com.huya.search.index.data.QueryResult;
import org.apache.lucene.index.IndexableField;

import java.util.List;


/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public class PartitionResult<T extends Iterable<IndexableField>> extends QueryResult<T> {


    public PartitionResult(List<T> collection) {
        super(collection);
    }

    @Override
    public String toString() {
        return "";
    }


}
