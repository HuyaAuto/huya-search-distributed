package com.huya.search.index.dsl.select;

import com.huya.search.index.meta.IndexFieldType;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/21.
 */
public class AntrlColumnSelectItem implements SelectItem {

    public static AntrlColumnSelectItem newInstance(String field, String alias, String expr, IndexFieldType indexFieldType, int number) {
        return new AntrlColumnSelectItem(field, alias, expr, indexFieldType, number);
    }

    private String field;

    private String alias;

    private String expr;

    private IndexFieldType indexFieldType;

    private int number;

    protected AntrlColumnSelectItem(String field, String alias, String expr, IndexFieldType indexFieldType, int number) {
        this.field = field;
        this.alias = alias;
        this.expr = expr;
        this.indexFieldType = indexFieldType;
        this.number = number;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getExpr() {
        return expr;
    }

    @Override
    public IndexFieldType type() {
        return indexFieldType;
    }


    @Override
    public int number() {
        return number;
    }
}
