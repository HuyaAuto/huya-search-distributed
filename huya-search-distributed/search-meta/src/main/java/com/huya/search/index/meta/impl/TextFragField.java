package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IndexSortField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class TextFragField extends IndexSortField {

    private TextField textField;

    private StoredField storedField;

    private SortedDocValuesField docValuesField;

    TextFragField(String name, String value, boolean sort, Analyzer analyzer) {
        super(name, sort);
        this.textField = new TextField(name, analyzer.tokenStream(name, value));
        this.storedField = new StoredField(name, value);
        if (isSort()) {
//            logger.info("=================================================" + sort);
            docValuesField = new SortedDocValuesField(name, new BytesRef(value));
        }
    }

    @Override
    public Collection<IndexableField> getLuceneField() {
        return isSort() ? Arrays.asList(textField, storedField, docValuesField) : Arrays.asList(textField, storedField);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.Text;
    }
}
