package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IndexSortField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
class StringKeyField extends IndexSortField {

    private static final String DEFAULT_VALUE = "";

    private StringField stringField;

    private SortedDocValuesField docValuesField;

    StringKeyField(String name, boolean sort) {
        this(name, DEFAULT_VALUE, sort);
    }

    StringKeyField(String name, String str, boolean sort) {
        this(name, new StringField(name, str, Field.Store.YES), sort);
    }

    StringKeyField(String name, StringField field, boolean sort) {
        super(name, sort);
        this.stringField = field;
        if (isSort()) docValuesField = new SortedDocValuesField(name, new BytesRef(field.stringValue()));
    }

    @Override
    public Collection<IndexableField> getLuceneField() {
        return isSort() ? Arrays.asList(stringField, docValuesField) : Collections.singleton(stringField);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.String;
    }
}
