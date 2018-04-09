package com.huya.search.index.strategy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.facing.subscriber.KafkaPullContext;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.NodePath;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.node.salvers.SalverNode;
import com.huya.search.rpc.RpcService;
import com.huya.search.settings.Settings;
import com.huya.search.tasks.PullTask;
import org.apache.avro.AvroRemoteException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 通过一致性哈希进行任务分配的策略
 * Created by zhangyiqun1@yy.com on 2017/11/3.
 */
@Singleton
public class RealMasterStrategy implements MasterStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RealMasterStrategy.class);

    private NodeConsistentHash consistentHash;

    private List<PullTask> notRunningList = new ArrayList<>();

    @Inject
    public RealMasterStrategy(@Named("Node") Settings settings) {

        int virtualNodeNum = settings.getAsInt("virtualNodeNum", 3);

        consistentHash = new NodeConsistentHash(virtualNodeNum) {
            @Override
            public void masterStrategyPullTask(NodeBaseEntry nodeBaseEntry, PullTask pullTask) {
                pullTask.start(PullTask.PullTaskAttr.newInstance(nodeBaseEntry));
            }

            @Override
            public void masterStrategyClosePullTask(NodeBaseEntry nodeBaseEntry, PullTask pullTask) {
                pullTask.stop(PullTask.PullTaskAttr.newInstance(nodeBaseEntry));
            }
        };
    }

    @Override
    public void addNode(SalverNode salverNode) {
        consistentHash.addNode(salverNode);
    }

    @Override
    public void removeNode(SalverNode salverNode) {
        consistentHash.removeNode(salverNode);
    }

//    @Override
//    public void lostNode(SalverNode salverNode) {
//        //todo 暂时什么也不做
//    }

    @Override
    public void createTask(PullTask pullTask) {
        notRunningList.add(pullTask);
    }

    @Override
    public void addTask(PullTask pullTask) {

    }

    @Override
    public void removeTask(PullTask pullTask) {
        consistentHash.removeAssignObject(pullTask);
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


//
//    private ZooKeeperOperator zo;
//

//
//    @Override
//    public void pull(String table, int partitionNum) {
//        for (int i = 0; i < partitionNum; i++) {
//            PullContext pullContext = KafkaPullContext.newInstance(table, i);
//            consistentHash.addAssignObject(pullContext);
//        }
//    }
//
//    @Override
//    public void stopPull(String table, int partitionNum) {
//        for (int i = 0; i < partitionNum; i++) {
//            PullContext pullContext = KafkaPullContext.newInstance(table, i);
//            consistentHash.removeAssignObject(pullContext);
//        }
//    }
//
//    @Override
//    public void pullShard(String table, int shardId) {
//        PullContext pullContext = KafkaPullContext.newInstance(table, shardId);
//        consistentHash.addAssignObject(pullContext);
//    }
//
//    @Override
//    public void stopPullShard(String table, int shardId) {
//        PullContext pullContext = KafkaPullContext.newInstance(table, shardId);
//        consistentHash.removeAssignObject(pullContext);
//    }
//
//    @Override
//    public void add(SalverNode salverNode) {
//        consistentHash.addNode(salverNode);
//    }
//
//    @Override
//    public void remove(SalverNode salverNode) {
//        consistentHash.removeNode(salverNode);
//    }
//
//    @Override
//    public void pullFromEnd(String table, int partitionNum) {
//        for (int i = 0; i < partitionNum; i++) {
//            PullContext pullContext = KafkaPullContext.newEndInstance(table, i);
//            consistentHash.addAssignObject(pullContext);
//        }
//    }
//
//    private void openPullTask(String serviceUrl, PullContext pullContext) {
//        CuratorFramework client = zo.getClient();
//        String pull = NodePath.pullShard(pullContext);
//        String pullTaskPath = NodePath.tasksPath(serviceUrl, pull);
//        try {
//            if (client.checkExists().forPath(pullTaskPath) == null) {
//                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(OPEN_ACL_UNSAFE).forPath(NodePath.tasksPath(serviceUrl, pull));
//            }
//        } catch (Exception e) {
//            LOG.error("create pull task error", e);
//        }
//    }
//
//    private void closePullTask(String serviceUrl, PullContext pullContext) {
//        CuratorFramework client = zo.getClient();
//        String pull = NodePath.pullShard(pullContext);
//        try {
//            client.delete().forPath(NodePath.tasksPath(serviceUrl, pull));
//        } catch (Exception e) {
//            LOG.error("create pull task error", e);
//        }
//    }
//
//
//    @Override
//    public void addNode(SalverNode salverNode) {
//        consistentHash.addNode(salverNode);
//    }
//
//    @Override
//    public void removeNode(SalverNode salverNode) {
//        consistentHash.removeNode(salverNode);
//    }
//
//    @Override
//    public void addTask(PullTask pullTask) {
//        consistentHash.addAssignObject(pullTask);
//    }
//
//    @Override
//    public void removeTask(PullTask pullTask) {
//        consistentHash.removeAssignObject(pullTask);
//    }
//
//    @Override
//    public void startStrategy() {
//
//    }
//
////    @Override
////    public void startTask(PullContext pullContext) {
////        consistentHash
////    }
////
////    @Override
////    public void stopTask(PullContext pullContext) {
////
////    }
//
//    @Override
//    public List<PullTask> allPullTasks() {
//        return null;
//    }
//
//    @Override
//    public List<PullTask> nodePullTasks(SalverNode salverNode) {
//        return null;
//    }
//
//    @Override
//    public PullTask getPullTasks(PullContext pullContext) {
//        return null;
//    }
}
