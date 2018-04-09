package com.huya.search.node;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public interface SalverNode {

    NodeBaseEntry getNodeEntry();

    void asJoin();

    void asLost();

    boolean isLost();
}
