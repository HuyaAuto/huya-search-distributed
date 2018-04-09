package com.huya.search.node;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public class RealSalverNode implements SalverNode {

    public static RealSalverNode newInstance(NodeBaseEntry nodeBaseEntry) {
        return new RealSalverNode(nodeBaseEntry);
    }

    private NodeBaseEntry nodeBaseEntry;

    private volatile boolean join = true;

    private RealSalverNode(NodeBaseEntry nodeBaseEntry) {
        this.nodeBaseEntry = nodeBaseEntry;
    }

    @Override
    public NodeBaseEntry getNodeEntry() {
        return nodeBaseEntry;
    }

    @Override
    public void asJoin() {
        join = true;
    }

    @Override
    public void asLost() {
        join = false;
    }

    @Override
    public boolean isLost() {
        return !join;
    }

}
