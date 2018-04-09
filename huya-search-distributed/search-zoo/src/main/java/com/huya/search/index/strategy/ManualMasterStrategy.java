package com.huya.search.index.strategy;

import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.salvers.SalverNode;
import com.huya.search.tasks.PullTask;

import java.util.List;
import java.util.Vector;

/**
 * 支持人工手动分配任务的策略
 * Created by zhangyiqun1@yy.com on 2017/12/29.
 */
public class ManualMasterStrategy implements MasterStrategy {



    @Override
    public void addNode(SalverNode salverNode) {

    }

    @Override
    public void removeNode(SalverNode salverNode) {

    }

    @Override
    public void createTask(PullTask pullTask) {

    }

    @Override
    public void addTask(PullTask pullTask) {

    }

    @Override
    public void removeTask(PullTask pullTask) {

    }

    @Override
    public void startStrategy() {

    }

    @Override
    public List<PullTask> allPullTasks() {
        return null;
    }

    @Override
    public List<PullTask> notRunningPullTasks() {
        return null;
    }

    @Override
    public List<PullTask> runningPullTasks() {
        return null;
    }

    @Override
    public List<PullTask> nodePullTasks(SalverNode salverNode) {
        return null;
    }

    @Override
    public PullTask getPullTasks(PullContext pullContext) {
        return null;
    }
}
