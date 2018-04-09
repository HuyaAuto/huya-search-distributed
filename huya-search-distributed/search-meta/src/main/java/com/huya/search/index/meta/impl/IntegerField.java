package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IndexSortField;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
class IntegerField extends IndexSortField {

    private static final int DEFAULT_VALUE = 0;

    private IntPoint intPoint;

    private StoredField storedField;

    private NumericDocValuesField docValuesField;

    IntegerField(String name, boolean sort) {
        this(name, DEFAULT_VALUE, sort);
    }

    IntegerField(String name, int value, boolean sort) {
        this(name, new IntPoint(name, value), sort);
    }

    IntegerField(String name, IntPoint field, boolean sort) {
        super(name, sort);
        int i = field.numericValue().intValue();
        this.intPoint = field;
        this.storedField = new StoredField(name, i);
        if (isSort()) this.docValuesField = new NumericDocValuesField(name, i);
    }

    @Override
    public Collection<IndexableField> getLuceneField() {
        return isSort() ? Arrays.asList(intPoint, storedField, docValuesField) : Arrays.asList(intPoint, storedField);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.Integer;
    }
}
