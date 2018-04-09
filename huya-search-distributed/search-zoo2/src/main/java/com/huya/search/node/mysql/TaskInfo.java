package com.huya.search.node.mysql;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/1.
 */
public class TaskInfo {

    private String table;
    private int shardId;
    private String serverUrl;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
