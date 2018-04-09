package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

import java.io.Reader;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class CountListener extends DefaultAggrFunUnit<Long> {

    public static CountListener newInstance(String field) {
        return new CountListener(field);
    }

    private long num = 0;

    private String field;

    private CountListener(String field) {
        this.field = field;
    }

    @Override
    public AggrFun getType() {
        return AggrFun.COUNT;
    }

    @Override
    public void add(IndexableField field) {
        num++;
    }

    @Override
    public IndexableField result() {
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
                return String.valueOf(getValue());
            }

            @Override
            public Reader readerValue() {
                return null;
            }

            @Override
            public Number numericValue() {
                return getValue();
            }
        };
    }

    @Override
    public Long getValue() {
        return num;
    }

    @Override
    public void clear() {
        num = 0;
    }

    @Override
    public CountListener clone() {
        return newInstance(field);
    }
}
