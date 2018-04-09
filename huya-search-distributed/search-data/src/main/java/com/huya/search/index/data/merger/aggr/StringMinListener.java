package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.data.merger.DefaultAggrFunUnit;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class StringMinListener extends DefaultAggrFunUnit<String> {

    public static StringMinListener newInstance(String field) {
        return new StringMinListener(field);
    }

    private String str = null;

    private String field;

    private StringMinListener(String field) {
        this.field = field;
    }

    @Override
    public AggrFun getType() {
        return AggrFun.MAX;
    }

    @Override
    public void add(IndexableField field) {
        if (str == null) {
            str = field.stringValue();
        }
        else {
            String other = field.stringValue();
            str = str.compareTo(other) > 0 ? other : str;
        }
    }

    @Override
    public IndexableField result() {
        return AggrIndexableField.strIndexableField(field, str);
    }

    @Override
    public String getValue() {
        return str;
    }

    @Override
    public void clear() {
        str = null;
    }

    @Override
    public StringMinListener clone() {
        return newInstance(field);
    }
}
