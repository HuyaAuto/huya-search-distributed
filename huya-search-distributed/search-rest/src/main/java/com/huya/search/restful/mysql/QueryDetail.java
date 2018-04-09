package com.huya.search.restful.mysql;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/20.
 */
public class QueryDetail {

    private int id;
    private String queryTime;
    private String sql;
    private long useTime;
    private int resultLen;
    private String table;
    private String exceptionMsg;

    public int getId() {
        return id;
    }

    public QueryDetail setId(int id) {
        this.id = id;
        return this;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public QueryDetail setQueryTime(String queryTime) {
        this.queryTime = queryTime;
        return this;
    }

    public String getSql() {
        return sql;
    }

    public QueryDetail setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public long getUseTime() {
        return useTime;
    }

    public QueryDetail setUseTime(long useTime) {
        this.useTime = useTime;
        return this;
    }

    public int getResultLen() {
        return resultLen;
    }

    public QueryDetail setResultLen(int resultLen) {
        this.resultLen = resultLen;
        return this;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public QueryDetail setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
        return this;
    }

    public String getTable() {
        return table;
    }

    public QueryDetail setTable(String table) {
        this.table = table;
        return this;
    }

    @Override
    public String toString() {
        return "QueryDetail{" +
                "id=" + id +
                ", queryTime='" + queryTime + '\'' +
                ", sql='" + sql + '\'' +
                ", useTime=" + useTime +
                ", resultLen=" + resultLen +
                ", table='" + table + '\'' +
                ", exceptionMsg='" + exceptionMsg + '\'' +
                '}';
    }
}
