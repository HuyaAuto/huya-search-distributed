package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IndexSortField;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
class DoubleField extends IndexSortField {

    private static final double DEFAULT_VALUE = 0.0d;

    private DoublePoint doublePoint;

    private StoredField storedField;

    private DoubleDocValuesField docValuesField;

    DoubleField(String name, boolean sort) {
        this(name, DEFAULT_VALUE, sort);
    }

    DoubleField(String name, double value, boolean sort) {
        this(name, new DoublePoint(name, value), sort);
    }

    DoubleField(String name, DoublePoint field, boolean sort) {
        super(name, sort);
        double d = field.numericValue().doubleValue();
        this.doublePoint = field;
        this.storedField = new StoredField(name, d);
        if (isSort()) docValuesField = new DoubleDocValuesField(name, d);
    }

    @Override
    public Collection<IndexableField> getLuceneField() {
        return isSort() ? Arrays.asList(doublePoint, storedField, docValuesField) : Arrays.asList(doublePoint, storedField);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.Double;
    }
}
