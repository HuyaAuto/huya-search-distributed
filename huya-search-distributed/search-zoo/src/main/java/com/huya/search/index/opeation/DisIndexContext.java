package com.huya.search.index.opeation;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public class DisIndexContext implements ExecutorContext {

    private String table;

    protected DisIndexContext() {}

    public DisIndexContext setTable(String table) {
        this.table = table;
        return this;
    }

    @Override
    public String getTable() {
        return table;
    }
}
