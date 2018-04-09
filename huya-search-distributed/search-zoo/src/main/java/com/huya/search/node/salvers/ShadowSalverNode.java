package com.huya.search.node.salvers;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
public class ShadowSalverNode {

    private SalverNode salverNode;

    private int hashCode;

    public ShadowSalverNode(SalverNode salverNode, int hashCode) {
        this.salverNode = salverNode;
        this.hashCode = hashCode;
    }

    public SalverNode getSalverNode() {
        return salverNode;
    }

    public int getHashCode() {
        return hashCode;
    }
}
