package com.huya.search.node;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public class RealSalverNodeFactory implements SalverNodeFactory {

    @Override
    public SalverNode create(NodeBaseEntry nodeBaseEntry) {
        return RealSalverNode.newInstance(nodeBaseEntry);
    }
}
