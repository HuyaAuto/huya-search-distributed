package com.huya.search.node.salvers;

import com.huya.search.node.NodeBaseEntry;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
public interface SalverNodeFactory {

    SalverNode create(NodeBaseEntry nodeBaseEntry);
}
