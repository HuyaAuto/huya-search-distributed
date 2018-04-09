package com.huya.search.node;

import com.huya.search.index.opeation.PullContext;
import com.huya.search.rpc.MasterRpcProtocol;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public interface TaskManager {

    void openPullTask(SalverNode salverNode, PullContext pullContext);

    void closePullTask(SalverNode salverNode, PullContext pullContext);

    void closePullTaskList(SalverNode salverNode, List<PullContext> pullContextList);

    void determineAndSyncPullTask(MasterRpcProtocol impl);

    void closeSyncPullTask();

}
