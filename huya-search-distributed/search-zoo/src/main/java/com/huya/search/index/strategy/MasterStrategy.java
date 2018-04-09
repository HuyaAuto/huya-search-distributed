package com.huya.search.index.strategy;

import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.salvers.SalverNode;
import com.huya.search.tasks.PullTask;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/3.
 */
public interface MasterStrategy {

//    void pull(String table, int partitionNum);
//
//    void stopPull(String table, int partitionNum);
//
//    void pullShard(String table, int shardId);
//
//    void stopPullShard(String table, int shardId);
//
//    void add(SalverNode salverNode);
//
//    void remove(SalverNode salverNode);
//
//    void pullFromEnd(String table, int partitionNum);

    /**
     * 为调度增加节点
     * @param salverNode 节点
     */
    void addNode(SalverNode salverNode);

    /**
     * 从调度移除节点
     * @param salverNode 节点
     */
    void removeNode(SalverNode salverNode);

//    /**
//     * 丢失节点
//     * @param salverNode 节点
//     */
//    void lostNode(SalverNode salverNode);

    /**
     * 穿件任务到调度
     * @param pullTask 任务
     */
    void createTask(PullTask pullTask);

    /**
     * 添加任务到工作区域
     * @param pullTask 任务
     */
    void addTask(PullTask pullTask);

    /**
     * 移除任务从工作区域
     * @param pullTask 任务
     */
    void removeTask(PullTask pullTask);

//    void startTask(PullContext pullContext);
//
//    void stopTask(PullContext pullContext);

    /**
     * 开始启动策略
     */
    void startStrategy();

    /**
     * 返回所有任务
     * @return 所有任务
     */
    List<PullTask> allPullTasks();

    /**
     * 返回所有未运行的任务
     * @return 所有未运行的任务
     */
    List<PullTask> notRunningPullTasks();

    /**
     * 返回所有运行的任务
     * @return 所有运行的任务
     */
    List<PullTask> runningPullTasks();

    /**
     * 返回指定节点的任务
     * @param salverNode 节点
     * @return 指定节点上的所有任务
     */
    List<PullTask> nodePullTasks(SalverNode salverNode);

    /**
     * 获取任务
     * @param pullContext 任务上下文
     * @return 任务
     */
    PullTask getPullTasks(PullContext pullContext);

}
