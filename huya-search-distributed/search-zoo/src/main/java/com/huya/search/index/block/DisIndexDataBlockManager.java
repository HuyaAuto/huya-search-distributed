package com.huya.search.index.block;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.facing.KafkaInfoService;
import com.huya.search.index.opeation.DisIndexContext;
import com.huya.search.index.opeation.DisShardIndexContext;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.node.salvers.SalverNode;
import com.huya.search.node.salvers.SalverNodeFactory;
import com.huya.search.node.salvers.Salvers;
import com.huya.search.util.JsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.huya.search.node.NodePath.SALVERS;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
@Singleton
public class DisIndexDataBlockManager extends DisDataBlockManager {

    private static final Logger LOG = LoggerFactory.getLogger(DisIndexDataBlockManager.class);

    private ZooKeeperOperator zo;

    private Salvers allSalvers ;

    private PathChildrenCache pathChildrenCache;

//    private Map<String, PathChildrenCache> pathChildrenCacheMap = new ConcurrentHashMap<>();

    private Map<String, List<PartitionInfo>> partitionInfosMap = new ConcurrentHashMap<>();

    private KafkaInfoService kafkaInfoService;

    private SalverNodeFactory salverNodeFactory;

    @Inject
    public DisIndexDataBlockManager(ZooKeeperOperator zo, KafkaInfoService kafkaInfoService, Salvers allSalvers, SalverNodeFactory salverNodeFactory) {
        this.zo = zo;
        this.kafkaInfoService = kafkaInfoService;
        this.allSalvers = allSalvers;
        this.salverNodeFactory = salverNodeFactory;
    }

    /**
     * 对整个集群分配拉取 table 的任务，但实际操作还是要看 allSalvers 内部的策略
     * @param disIndexContext 分布式拉取任务上下文
     * @throws SearchException 异常
     */
    @Override
    public void insert(DisIndexContext disIndexContext) throws SearchException {
        String table = disIndexContext.getTable();
        pullDistributionKafkaData(table);
    }

    /**
     * 对整个集群停止拉取 table 的任务，但实际操作还是要看 allSalvers 内部的策略
     * @param disIndexContext 分布式拉去任务上下文
     * @throws SearchException 异常
     */
    @Override
    public void stopInsert(DisIndexContext disIndexContext) throws SearchException {
        String table = disIndexContext.getTable();
        stopPullDistributionKafkaData(table);
    }

    @Override
    public void insertFromEnd(DisIndexContext disIndexContext) throws SearchException {
        String table = disIndexContext.getTable();
        pullDistributionKafkaDataFromEnd(table);
    }

    /**
     * 对整个集群分配拉取 table 的任务，但实际操作还是要看 allSalvers 内部的策略
     * @param disIndexContext 分布式拉取任务上下文
     * @throws SearchException 异常
     */
    @Override
    public void insertShard(DisShardIndexContext disIndexContext) throws SearchException {
        String table = disIndexContext.getTable();
        int shardId = disIndexContext.getShardId();
        pullDistributionKafkaData(table, shardId);
    }

    /**
     * 对整个集群停止拉取 table 的任务，但实际操作还是要看 allSalvers 内部的策略
     * @param disIndexContext 分布式拉去任务上下文
     * @throws SearchException 异常
     */
    @Override
    public void stopInsertShard(DisShardIndexContext disIndexContext) throws SearchException {
        String table = disIndexContext.getTable();
        int shardId = disIndexContext.getShardId();
        stopPullDistributionKafkaData(table, shardId);
    }

    private List<PartitionInfo> getPartitionInfos(String table) {
        List<PartitionInfo> partitionInfos = partitionInfosMap.get(table);
        if (partitionInfos == null) {
            partitionInfos = kafkaInfoService.getPartitionInfo(table);
            partitionInfos.forEach(partitionInfo -> LOG.info(partitionInfo.toString()));
            partitionInfosMap.put(table, partitionInfos);
        }
        return partitionInfos;
    }


    private void pullDistributionKafkaData(String table) {
        int partitionNum = getPartitionInfos(table).size();
        allSalvers.pull(table, partitionNum);
    }

    private void pullDistributionKafkaDataFromEnd(String table) {
        int partitionNum = getPartitionInfos(table).size();
        allSalvers.pullFromEnd(table, partitionNum);
    }

    private void stopPullDistributionKafkaData(String table) {
        int partitionNum = getPartitionInfos(table).size();
        allSalvers.stopPull(table, partitionNum);
    }

    private void pullDistributionKafkaData(String table, int shardId) {
        int partitionNum = getPartitionInfos(table).size();

        assert shardId >= 0 && shardId <= partitionNum - 1;

        allSalvers.pullShard(table, shardId);
    }

    private void stopPullDistributionKafkaData(String table, int shardId) {
        int partitionNum = getPartitionInfos(table).size();

        assert shardId >= 0 && shardId <= partitionNum - 1;

        allSalvers.stopPullShard(table, shardId);
    }

    @Override
    public Salvers getAllSalvers() {
        return allSalvers;
    }

    @Override
    protected void doStart() throws SearchException {
        //获取 Kafka 的相关信息
        kafkaInfoService.start();
        //同步所有的子节点信息
        syncSalvers();
    }

    /**
     * 实时同步节点【加入】、与【丢失】动作
     */
    private void syncSalvers() {
        CuratorFramework client = zo.getClient();
        pathChildrenCache = new PathChildrenCache(client, SALVERS, true);
        pathChildrenCache.getListenable().addListener((aClient, event) -> {
            PathChildrenCacheEvent.Type type = event.getType();
            if (type == PathChildrenCacheEvent.Type.CHILD_ADDED || type == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                NodeBaseEntry nodeBaseEntry = JsonUtil.getObjectMapper().readValue(event.getData().getData(), NodeBaseEntryImpl.class);
                switch (event.getType()) {
                    case CHILD_ADDED:
                        if (this.started()) {
                            SalverNode salverNode = salverNodeFactory.create(nodeBaseEntry);
//                            String taskPath = NodePath.salverReplaceTasksPath(event.getData().getPath());
//                            syncSalverTask(salverNode, taskPath);
                            addSalver(salverNode);
                        }
                        break;
                    case CHILD_REMOVED:
                        if (this.started()) {
//                            PathChildrenCache cache = pathChildrenCacheMap.remove(event.getData().getPath());
//                            cache.close();
                            lostSalver(nodeBaseEntry);
                        }
                        break;
                }
            }
        });
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            LOG.error("SALVERS pathChildrenCache error", e);
        }

    }

//    /**
//     * 实时同步指定节点的任务分配
//     * @param salverNode 指定节点
//     * @param tasksPath 任务路径
//     */
//    private void syncSalverTask(SalverNode salverNode, String tasksPath) {
//        CuratorFramework client = zo.getClient();
//        assert salverNode != null;
//        final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, tasksPath, true);
//        pathChildrenCache.getListenable().addListener((aClient, event) -> {
//            PathChildrenCacheEvent.Type type = event.getType();
//            if ((type == PathChildrenCacheEvent.Type.CHILD_ADDED || type == PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
//                String taskPath = event.getData().getPath();
//                if (!taskPath.endsWith(NodePath.PULL)) {
//                    String taskName = taskName(tasksPath, event.getData().getPath());
//                    String temp[] = taskName.split("_");
//                    assert temp.length >= 3;
//                    String table = tableName(temp);
//                    long unixTime = Long.parseLong(temp[temp.length - 2]);
//                    int shardId = Integer.parseInt(temp[temp.length - 1]);
//                    switch (event.getType()) {
//                        case CHILD_ADDED:
//                            salverNode.add(table, shardId, unixTime);
//                            break;
//                        case CHILD_REMOVED:
//                            salverNode.remove(table, shardId, unixTime);
//                            break;
//                    }
//                }
//            }
//        });
//        try {
//            pathChildrenCacheMap.put(tasksPath, pathChildrenCache);
//            pathChildrenCache.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 获取表名
//     * @param temp  任务名下划线分割后字符串数组
//     * @return 表名
//     */
//    private String tableName(String[] temp) {
//        if (temp.length > 3) {
//            StringBuilder table = new StringBuilder(temp[0]);
//            for (int i = 1; i <temp.length - 2 ; i++) {
//                table.append("_").append(temp[i]);
//            }
//            return table.toString();
//        }
//        else {
//            return temp[0];
//        }
//    }
//
//    /**
//     * 获取任务名称
//     * @param path 任务目录路径
//     * @param taskPath 任务路径
//     * @return 任务名称
//     */
//    private String taskName(String path, String taskPath) {
//        return taskPath.substring(path.length() + 1);
//    }

    /**
     * 添加节点
     * @param salverNode 节点
     */
    private void addSalver(SalverNode salverNode) {
        allSalvers.join(salverNode);
        listenerList.forEach(listener -> listener.afterAddSalver(salverNode));
        LOG.info("add {} to distributed search", salverNode.getNodeEntry().getServiceUrl());
    }

    /**
     * 丢失节点
     * @param nodeBaseEntry 节点实体
     */
    private void lostSalver(NodeBaseEntry nodeBaseEntry) {
        SalverNode salverNode = allSalvers.lost(nodeBaseEntry);
        listenerList.forEach(listener -> listener.afterLostSalver(salverNode));
        LOG.info("lost {} from distributed search", salverNode.getNodeEntry().getServiceUrl());
    }



    @Override
    protected void doStop() throws S    earchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        try {
//            for (PathChildrenCache childrenCache : pathChildrenCacheMap.values()) {
//                childrenCache.close();
//            }
            pathChildrenCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String getName() {
        return "DisIndexDataBlockManager";
    }

}
