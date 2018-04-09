package com.huya.search.index.data;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

import java.io.Reader;
import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class SingleQueryResult {

    public static QueryResult<? extends Iterable<IndexableField>> SingleNum(String field, Number number) {
        return new QueryResult<>(Collections.singletonList(() -> new Iterator<IndexableField>() {

            private int i = 1;

            @Override
            public boolean hasNext() {
                return i > 0;
            }

            @Override
            public IndexableField next() {
                i--;
                return createNumberIndexableField(field, number);
            }
        }));
    }

    public static QueryResult<? extends Iterable<IndexableField>> SingleLineNum(Set<String> fields, Number number) {
        List<IndexableField> temp = new ArrayList<>();
        fields.forEach(field -> temp.add(createNumberIndexableField(field, number)));
        return new QueryResult<>(Collections.singletonList(temp));
    }

    public static IndexableField createStringIndexableField(String field, String str) {
        return new IndexableField() {
            @Override
            public String name() {
                return field;
            }

            @Override
            public IndexableFieldType fieldType() {
                return null;
            }

            @Override
            public TokenStream tokenStream(Analyzer analyzer, TokenStream reuse) {
                return null;
            }

            @Override
            public float boost() {
                return 0;
            }

            @Override
            public BytesRef binaryValue() {
                return null;
            }

            @Override
            public String stringValue() {
                return str;
            }

            @Override
            public Reader readerValue() {
                return null;
            }

            @Override
            public Number numericValue() {
                return null;
            }
        };
    }

    public static IndexableField createNumberIndexableField(String field, Number number) {
        return new IndexableField() {
            @Override
            public String name() {
                return field;
            }

            @Override
            public IndexableFieldType fieldType() {
                return null;
            }

            @Override
            public TokenStream tokenStream(Analyzer analyzer, TokenStream reuse) {
                return null;
            }

            @Override
            public float boost() {
                return 0;
            }

            @Override
            public BytesRef binaryValue() {
                return null;
            }

            @Override
            public String stringValue() {
                return String.valueOf(number);
            }

            @Override
            public Reader readerValue() {
                return null;
            }

            @Override
            public Number numericValue() {
                return number;
            }
        };
    }
}
