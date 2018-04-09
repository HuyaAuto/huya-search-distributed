package com.huya.search.rpc;

import com.huya.search.index.data.QueryResult;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
public class QueryDetailResult {

    private RpcResult rpcResult;

    private String table;

    private long useTime;

    private int hitShard;

    private int hitNode;

    public RpcResult getRpcResult() {
        return rpcResult;
    }

    public QueryDetailResult setRpcResult(RpcResult rpcResult) {
        this.rpcResult = rpcResult;
        return this;
    }

    public String getTable() {
        return table;
    }

    public QueryDetailResult setTable(String table) {
        this.table = table;
        return this;
    }

    public long getUseTime() {
        return useTime;
    }

    public QueryDetailResult setUseTime(long useTime) {
        this.useTime = useTime;
        return this;
    }

    public int getHitShard() {
        return hitShard;
    }

    public QueryDetailResult setHitShard(int hitShard) {
        this.hitShard = hitShard;
        return this;
    }

    public int getHitNode() {
        return hitNode;
    }

    public QueryDetailResult setHitNode(int hitNode) {
        this.hitNode = hitNode;
        return this;
    }

    public QueryResult getQueryResult() {
        return new QueryResult<>(getRpcResult().getResult()).setRunTime(useTime);
    }
}
