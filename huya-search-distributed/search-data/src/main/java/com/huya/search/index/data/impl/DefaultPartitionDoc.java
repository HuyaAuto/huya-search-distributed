package com.huya.search.index.data.impl;

import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class DefaultPartitionDoc implements PartitionDoc {

    public static DefaultPartitionDoc newInstance(Iterable<IndexableField> iterable) {
        return new DefaultPartitionDoc(iterable);
    }

    protected Iterable<IndexableField> iterable;

    protected DefaultPartitionDoc(Iterable<IndexableField> iterable) {
        this.iterable = iterable;
    }


    public IndexableField getField(String name) {
        for (IndexableField field : iterable) {
            if (name.equals(field.name())) {
                return field;
            }
        }
        return null;
    }

    @Override
    public Iterable<IndexableField> getDocument() {
        return iterable;
    }
}
