package com.huya.search.index.opeation;

import com.huya.search.index.opeation.ExecutorContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public class DisRefreshContext implements ExecutorContext {

    private String table;

    protected DisRefreshContext() {}

    public DisRefreshContext setTable(String table) {
        this.table = table;
        return this;
    }

    @Override
    public String getTable() {
        return table;
    }
}
