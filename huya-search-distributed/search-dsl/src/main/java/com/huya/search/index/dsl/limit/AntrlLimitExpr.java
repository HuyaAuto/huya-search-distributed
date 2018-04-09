package com.huya.search.index.dsl.limit;

public class AntrlLimitExpr extends LimitExpr {

    public static AntrlLimitExpr newInstance(int top) {
        return new AntrlLimitExpr(top);
    }

    public static AntrlLimitExpr newInstance(int from, int len) {
        return new AntrlLimitExpr(from, len);
    }

    public static AntrlLimitExpr newInstance() {
        return new AntrlLimitExpr();
    }

    /**
     * 从 0 计数
     * 例如:
     * 查询 top 50  ===>  from = 0 , to = 49
     */
    private int from;

    private int to;

    private boolean queryAll;

    /**
     * 查询前 top 条记录
     * @param top
     */
    private AntrlLimitExpr(int top) {
        this.from = 0;
        this.to   = top - 1;
        this.queryAll = false;
    }

    /**
     * 从 from 往后查询 len 条记录
     * @param from 其实偏移，从 0 计数
     * @param len 往后查询长度
     */
    private AntrlLimitExpr(int from, int len) {
        this.from = from;
        this.to   = from + len - 1;
        this.queryAll = false;
    }

    private AntrlLimitExpr() {
        this.queryAll = true;
    }

    @Override
    public int to() {
        return to;
    }

    @Override
    public int from() {
        return from;
    }

    @Override
    public boolean selectAll() {
        return queryAll;
    }
}
