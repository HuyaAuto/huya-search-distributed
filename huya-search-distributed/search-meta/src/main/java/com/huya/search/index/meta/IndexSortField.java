package com.huya.search.index.meta;

public abstract class IndexSortField extends IndexField {

    private boolean sort;

    protected IndexSortField(String name, boolean sort) {
        super(name);
        this.sort = sort;
    }

    public boolean isSort() {
        return sort;
    }
}
