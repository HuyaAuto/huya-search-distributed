package com.huya.search.tasks;

import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.NodeBaseEntry;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/4.
 */
public interface TaskNodeAssigner {

    NodeBaseEntry getNode(PullContext pullContext);
}
