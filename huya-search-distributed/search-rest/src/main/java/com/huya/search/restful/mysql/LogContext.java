package com.huya.search.restful.mysql;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/2.
 */
public class LogContext {

    private int id;
    private String table;
    private String columnKeys;
    private String context;

    public int getId() {
        return id;
    }

    public LogContext setId(int id) {
        this.id = id;
        return this;
    }

    public String getTable() {
        return table;
    }

    public LogContext setTable(String table) {
        this.table = table;
        return this;
    }

    public String getColumnKeys() {
        return columnKeys;
    }

    public LogContext setColumnKeys(String columnKeys) {
        this.columnKeys = columnKeys;
        return this;
    }

    public String getContext() {
        return context;
    }

    public LogContext setContext(String context) {
        this.context = context;
        return this;
    }
}
