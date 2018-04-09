package com.huya.search.index.dsl.group;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/11.
 */
public class AntrlGroupByItem implements GroupByItem {

    public static AntrlGroupByItem newInstance(String field) {
        return new AntrlGroupByItem(field);
    }

    private String field;

    public AntrlGroupByItem(String field) {
        this.field = field;
    }

    @Override
    public Collection<String> needExtracted() {
        return Collections.singleton(field);
    }

}
