package com.huya.search.index.meta;

import com.huya.search.util.SortedList;

public class MetaFollowerList extends SortedList<MetaFollower> {

    @Override
    public int compare(MetaFollower t1, MetaFollower t2) {
        return Integer.compare(t1.priority(), t2.priority());
    }
}
