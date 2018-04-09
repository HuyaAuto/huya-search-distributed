package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.facing.subscriber.KafkaPullContext;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.mysql.TaskInfo;
import com.huya.search.node.mysql.mapper.TaskInfoMapper;
import com.huya.search.node.mysql.mapper.TaskInfoMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/1.
 */
@Singleton
public class TasksBoxImpl implements TasksBox {

    private static final Logger LOG = LoggerFactory.getLogger(TasksBoxImpl.class);

    private static final TaskInfoMapper TASK_INFO_MAPPER = new TaskInfoMapperImpl();

    @Inject
    public TasksBoxImpl() {}

    private Map<PullContext, PullTaskState> stateMap = new ConcurrentHashMap<>();

    private Map<PullContext, TaskInfo> taskInfoMap = new ConcurrentHashMap<>();

    @Override
    public void determineTasks(PullContext pullContext) {
        stateMap.put(pullContext, PullTaskState.newInstance(pullContext));
    }

    @Override
    public void updateTask(PullTaskState pullTaskState) {
        stateMap.put(pullTaskState.getPullContext(), pullTaskState);
    }

    @Override
    public SalverNodesQueryInfo getSalverNodesQueryInfo(String table) {
        SalverNodesQueryInfo queryInfo = new SalverNodesQueryInfo();
        final int[] shardNum = {0};
        Set<SalverNode> nodes = stateMap.values().stream()
                .filter(pullTaskState -> {
                    SalverNode salverNode = pullTaskState.getSalverNode();
                    if (Objects.equals(pullTaskState.getPullContext().getTable(), table) && salverNode != null && ! salverNode.isLost()) {
                        shardNum[0]++;
                        return true;
                    }
                    else {
                        return false;
                    }
                })
                .map(PullTaskState::getSalverNode)
                .collect(Collectors.toSet());
        return queryInfo.setNodeSet(nodes).setShardNum(shardNum[0]);
    }

    @Override
    public List<Integer> getShardIdList(SalverNode salverNode, String table) {
        List<Integer> shardNumList = new ArrayList<>();
        stateMap.values().forEach(pullTaskState -> {
            if (Objects.equals(pullTaskState.getPullContext().getTable(), table) && Objects.equals(pullTaskState.getSalverNode(), salverNode)) {
                shardNumList.add(pullTaskState.getPullContext().getShardId());
            }
        });
        return shardNumList;
    }

    @Override
    public void loadAllTaskInfo() {
        List<TaskInfo> taskInfoList = TASK_INFO_MAPPER.getAllTaskInfo();
        taskInfoList.forEach(taskInfo -> {
            PullContext pullContext = KafkaPullContext.newInstance(taskInfo.getTable(), taskInfo.getShardId());
            taskInfoMap.put(pullContext, taskInfo);
        });
    }

    @Override
    public String getServerUrl(PullContext pullContext) {
        TaskInfo taskInfo =  taskInfoMap.get(pullContext);
        if (taskInfo != null) {
            return taskInfo.getServerUrl();
        }
        else {
            LOG.info(pullContext.toString());
            LOG.info(taskInfoMap.keySet().toString());
        }
        return null;
    }

    @Override
    public Iterator<PullTaskState> iterator() {
        return stateMap.values().iterator();
    }
}
