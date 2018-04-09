package com.huya.search.index.data.impl;

import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public interface PartitionDoc {

    IndexableField getField(String name);

    Iterable<IndexableField> getDocument();
}
