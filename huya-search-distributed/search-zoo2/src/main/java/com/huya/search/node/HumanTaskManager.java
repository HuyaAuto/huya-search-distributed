package com.huya.search.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.facing.subscriber.KafkaPullContext;
import com.huya.search.index.block.ShardInfos;
import com.huya.search.index.block.ShardInfosService;
import com.huya.search.index.block.TableShardServiceAble;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.mysql.TaskInfo;
import com.huya.search.rpc.MasterRpcProtocol;
import com.huya.search.service.TaskState;
import com.huya.search.util.JsonUtil;
import org.apache.avro.AvroRemoteException;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 映射到任务管理界面，用于人类管理任务的接口，相反是通过算法自动管理的接口
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
@Singleton
public class HumanTaskManager implements TaskManager {

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private TableShardServiceAble tableShardServiceAble;

    private MonitorMeta monitorMeta;

    private Cluster cluster;

    private TasksBox tasksBox;

    private TimerTask syncTask;

    @Inject
    public HumanTaskManager(TableShardServiceAble tableShardServiceAble, @Named("MasterMonitorMeta") MonitorMeta monitorMeta,
                            Cluster cluster, TasksBox tasksBox) {
        this.tableShardServiceAble = tableShardServiceAble;
        this.monitorMeta = monitorMeta;
        this.cluster = cluster;
        this.tasksBox = tasksBox;
    }

    @Override
    public void openPullTask(SalverNode salverNode, PullContext pullContext) {
        PullTaskState pullTaskState = PullTaskState.newInstance(pullContext, TaskState.RUNNING, salverNode);
        tasksBox.updateTask(pullTaskState);
    }

    @Override
    public void closePullTask(SalverNode salverNode, PullContext pullContext) {
        PullTaskState pullTaskState = PullTaskState.newInstance(pullContext, TaskState.NOT_RUNNING, salverNode);
        tasksBox.updateTask(pullTaskState);
    }

    @Override
    public void closePullTaskList(SalverNode salverNode, List<PullContext> pullContextList) {
        pullContextList.forEach(pullContext -> closePullTask(salverNode, pullContext));
    }

    /**
     * 确定任务，先同步子节点任务列表
     * 之后每分钟进行一次检查
     */
    @Override
    public void determineAndSyncPullTask(MasterRpcProtocol impl) {
        determineTasks();
        loadAllTaskInfo();
        try {
            syncPullTask(impl.syncPullTask().toString());
            startUpPullTasks(impl);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
        }

        syncTask = new SyncPullTask(impl);
        scheduledExecutorService.scheduleAtFixedRate(syncTask, 0, 60, TimeUnit.SECONDS);
    }

    private void loadAllTaskInfo() {
        tasksBox.loadAllTaskInfo();
    }

    private void startUpPullTasks(MasterRpcProtocol impl) {
        tasksBox.iterator().forEachRemaining(pullTaskState -> {
            if (pullTaskState.getTaskState() == TaskState.NOT_RUNNING) {
                SalverNode salverNode = pullTaskState.getSalverNode();
                PullContext pullContext = pullTaskState.getPullContext();
                if (salverNode == null) {
                    String serverUrl = tasksBox.getServerUrl(pullContext);
                    if (serverUrl != null) {
                        try {
                            impl.openPullTask(serverUrl, pullContext.getTable(), pullContext.getShardId(), "");
                        } catch (AvroRemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void closeSyncPullTask() {
        syncTask.cancel();
    }

    private void determineTasks() {
        monitorMeta.iterator().forEachRemaining(
                this::addTableTasks
        );
    }

    private void addTableTasks(TimelineMetaDefine metaDefine) {
        if (metaDefine.isOpen()) {
            String table = metaDefine.getTable();
            ShardInfosService shardInfosService = tableShardServiceAble.getShardInfos(table);
            ShardInfos shardInfos = shardInfosService.getShardInfos();
            for (int i = 0; i < shardInfos.shardNum(); i++) {
                addTask(KafkaPullContext.newEndInstance(table, i));
            }
        }
    }

    private void addTask(PullContext pullContext) {
        tasksBox.determineTasks(pullContext);
    }

    private void syncPullTask(String pullTaskJson) {
        try {
            ObjectNode objectNode = (ObjectNode) JsonUtil.getObjectMapper().readTree(pullTaskJson);
            objectNode.fields().forEachRemaining(entry -> {
                String serverUrl = entry.getKey();
                SalverNode salverNode = cluster.getSalverNode(NodeBaseEntryImpl.newInstance(serverUrl));
                JsonNode jsonNode = entry.getValue();
                if (jsonNode.isObject()) { //其他情况见 SyncPullTaskState
                    ObjectNode taskObject = (ObjectNode) jsonNode;
                    taskObject.fields().forEachRemaining(tableEntry -> {
                        String table = tableEntry.getKey();
                        ArrayNode arrayNode = (ArrayNode) tableEntry.getValue();
                        arrayNode.iterator().forEachRemaining(value -> {
                            int shardId = value.intValue();
                            PullContext pullContext = KafkaPullContext.newInstance(table, shardId);
                            PullTaskState pullTaskState = PullTaskState.newInstance(pullContext, TaskState.RUNNING, salverNode);
                            tasksBox.updateTask(pullTaskState);
                        });
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SyncPullTask extends TimerTask {

        private MasterRpcProtocol impl;

        SyncPullTask(MasterRpcProtocol impl) {
            this.impl = impl;
        }

        @Override
        public void run() {
            try {
                syncPullTask(impl.syncPullTask().toString());
            } catch (AvroRemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
