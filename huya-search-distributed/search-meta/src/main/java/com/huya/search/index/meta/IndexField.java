package com.huya.search.index.meta;

import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public abstract class IndexField {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    protected IndexField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Collection<IndexableField> getLuceneField();

    public abstract IndexFieldType getFieldType();
}
