package com.huya.search.index.meta.impl;

import com.huya.search.index.meta.IndexFieldType;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class DateField extends LongField {

    protected DateField(String name, long value, boolean sort) {
        super(name, value, sort);
    }

    @Override
    public IndexFieldType getFieldType() {
        return IndexFieldType.Date;
    }
}
