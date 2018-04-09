package com.huya.search.node;

import com.huya.search.index.opeation.PullContext;

import java.util.List;


/**
 * Created by zhangyiqun1@yy.com on 2018/2/28.
 */
public interface TasksBox extends Iterable<PullTaskState> {

    /**
     * 确认系统需要运行的任务（跑不跑不重要，先加入 box 里）
     * @param pullContext
     */
    void determineTasks(PullContext pullContext);

    /**
     * 更新任务状态
     * @param pullTaskState
     */
    void updateTask(PullTaskState pullTaskState);

    /**
     * 获取节点查询信息
     * @param table
     * @return
     */
    SalverNodesQueryInfo getSalverNodesQueryInfo(String table);

    /**
     * 获取节点上某个表的分片 id 列表
     * @param salverNode
     * @param table
     * @return
     */
    List<Integer> getShardIdList(SalverNode salverNode, String table);

    /**
     * 加载所有的任务信息
     */
    void loadAllTaskInfo();

    /**
     * 获取服务地址
     * @param pullContext
     * @return
     */
    String getServerUrl(PullContext pullContext);
}
