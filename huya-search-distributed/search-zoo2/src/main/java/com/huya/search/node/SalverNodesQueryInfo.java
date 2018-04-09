package com.huya.search.node;

import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public class SalverNodesQueryInfo {

    private int shardNum;

    private Set<SalverNode> nodeSet;

    public int getShardNum() {
        return shardNum;
    }

    public SalverNodesQueryInfo setShardNum(int shardNum) {
        this.shardNum = shardNum;
        return this;
    }

    public Set<SalverNode> getNodeSet() {
        return nodeSet;
    }

    public SalverNodesQueryInfo setNodeSet(Set<SalverNode> nodeSet) {
        this.nodeSet = nodeSet;
        return this;
    }
}
