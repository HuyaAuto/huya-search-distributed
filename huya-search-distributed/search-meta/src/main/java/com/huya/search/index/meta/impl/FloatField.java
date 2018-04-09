package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IndexSortField;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
class FloatField extends IndexSortField {

    private static final float DEFAULT_VALUE = 0.0f;

    private FloatPoint floatPoint;

    private StoredField storedField;

    private FloatDocValuesField docValuesField;

    FloatField(String name, boolean sort) {
        this(name, DEFAULT_VALUE, sort);
    }

    FloatField(String name, float value, boolean sort) {
        this(name, new FloatPoint(name, value), sort);
    }

    FloatField(String name, FloatPoint field, boolean sort) {
        super(name, sort);
        float f = field.numericValue().floatValue();
        this.floatPoint = field;
        this.storedField = new StoredField(name, f);
        if (isSort()) this.docValuesField = new FloatDocValuesField(name, f);
    }

    @Override
    public Collection<IndexableField> getLuceneField() {
        return isSort() ? Arrays.asList(floatPoint, storedField, docValuesField) : Arrays.asList(floatPoint, storedField);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.Float;
    }
}
