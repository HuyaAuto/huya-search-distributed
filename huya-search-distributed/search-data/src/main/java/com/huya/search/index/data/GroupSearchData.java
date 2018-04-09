package com.huya.search.index.data;

import org.apache.lucene.index.IndexableField;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/13.
 */
public class GroupSearchData<T extends Iterable<IndexableField>> extends SearchData<T> {

    private GroupKey groupKey;

    public GroupSearchData(GroupKey groupKey, List<T> list) {
        super(list);
        this.groupKey = groupKey;
    }

    public GroupKey getGroupKey() {
        return groupKey;
    }
}
