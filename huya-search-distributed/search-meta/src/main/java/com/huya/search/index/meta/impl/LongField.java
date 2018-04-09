package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IndexSortField;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
class LongField extends IndexSortField {

    private static final long DEFAULT_VALUE = 0L;

    private LongPoint longPoint;

    private StoredField storedField;

    private NumericDocValuesField docValuesField;

    LongField(String name, boolean sort) {
        this(name, DEFAULT_VALUE, sort);
    }

    LongField(String name, long value, boolean sort) {
        this(name, new LongPoint(name, value), sort);
    }

    LongField(String name, LongPoint field, boolean sort) {
        super(name, sort);
        long l = field.numericValue().longValue();
        this.longPoint = field;
        this.storedField = new StoredField(name, l);
        if (isSort()) docValuesField = new NumericDocValuesField(name, l);
    }

    @Override
    public Collection<IndexableField> getLuceneField() {
        return isSort() ? Arrays.asList(longPoint, storedField, docValuesField) : Arrays.asList(longPoint, storedField);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.Long;
    }
}
