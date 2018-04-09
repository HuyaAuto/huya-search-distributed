package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.rpc.MasterRpcProtocol;

import java.util.List;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
@Singleton
public class RealTaskDistributionService extends TaskDistributionService {

    private Cluster cluster;

    private TaskManager taskManager;

    @Inject
    public RealTaskDistributionService(ZooKeeperOperator zo, Cluster cluster, TaskManager taskManager) {
        super(zo);
        this.cluster = cluster;
        this.taskManager = taskManager;
    }

    @Override
    public Set<SalverNode> getAllSalvers() {
        return cluster.getAllSalvers();
    }

    @Override
    public Set<SalverNode> getSalvers(String table) {
        return cluster.getSalvers(table);
    }

    @Override
    public SalverNode getSalverNode(NodeBaseEntry nodeBaseEntry) {
        return cluster.getSalverNode(nodeBaseEntry);
    }

    @Override
    public SalverNodesQueryInfo getSalverNodesQueryInfo(String table) {
        return cluster.getSalverNodesQueryInfo(table);
    }

    public List<Integer> getShardIdList(SalverNode salverNode, String table) {
        return cluster.getShardIdList(salverNode, table);
    }

//    @Override
//    public void openPullTask(String serviceUrl, PullContext pullContext) throws TaskException {
//        taskManager.openPullTask(serviceUrl, pullContext);
//    }
//
//    @Override
//    public void closePullTask(String serviceUrl, PullContext pullContext) throws TaskException {
//        taskManager.closePullTask(serviceUrl, pullContext);
//    }
//
//    @Override
//    public void tasksShutdown() {
//        taskManager.tasksShutdown();
//    }

    @Override
    public void openPullTask(SalverNode salverNode, PullContext pullContext) {
        taskManager.openPullTask(salverNode, pullContext);
    }

    @Override
    public void closePullTask(SalverNode salverNode, PullContext pullContext) {
        taskManager.closePullTask(salverNode, pullContext);
    }

    @Override
    public void closePullTaskList(SalverNode salverNode, List<PullContext> pullContextList) {
        taskManager.closePullTaskList(salverNode, pullContextList);
    }

    @Override
    public void determineAndSyncPullTask(MasterRpcProtocol impl) {
        taskManager.determineAndSyncPullTask(impl);
    }

    @Override
    public void closeSyncPullTask() {
        taskManager.closeSyncPullTask();
    }

    @Override
    public void addSalver(NodeBaseEntry nodeBaseEntry) {
        cluster.addSalver(nodeBaseEntry);
    }

    @Override
    public void lostSalver(NodeBaseEntry nodeBaseEntry) {
        cluster.lostSalver(nodeBaseEntry);
    }

    @Override
    public String getName() {
        return "RealTaskDistributionService";
    }
}
