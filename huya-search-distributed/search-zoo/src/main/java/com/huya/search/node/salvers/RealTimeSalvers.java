package com.huya.search.node.salvers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.facing.subscriber.KafkaPullContext;
import com.huya.search.index.strategy.MasterStrategy;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.tasks.PullTask;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/25.
 */
@Singleton
public class RealTimeSalvers implements Salvers {

    private Map<NodeBaseEntry, SalverNode> salverNodes = new HashMap<>();

    private MasterStrategy masterStrategy;

    @Inject
    private RealTimeSalvers(MasterStrategy masterStrategy) {
        this.masterStrategy = masterStrategy;
    }

    @Override
    public int salversNum() {
        return salverNodes.size();
    }

    @Override
    public synchronized void join(SalverNode salver) {
        SalverNode existNode = salverNodes.get(salver.getNodeEntry());
        if (existNode == null) {
            salverNodes.put(salver.getNodeEntry(), salver);
            masterStrategy.addNode(salver);
        }
        else if (existNode.isLost()) {
            existNode.join();
        }
    }

    @Override
    public synchronized SalverNode lost(NodeBaseEntry nodeBaseEntry) {
        SalverNode existNode = salverNodes.get(nodeBaseEntry);
        assert existNode != null;
        if (!existNode.isLost()) {
            existNode.lost();
        }
        return existNode;
    }

    @Override
    public Set<SalverNode> getSalvers() {
        return new HashSet<>(salverNodes.values());
    }

    @Override
    public Set<SalverNode> getSalvers(final String table) {
        return salverNodes.values().stream().filter(salverNode -> salverNode.contains(table)).collect(Collectors.toSet());
    }

    @Override
    public Set<SalverNode> getSalversWithOut(String table) {
        return salverNodes.values().stream().filter(salverNode -> !salverNode.contains(table)).collect(Collectors.toSet());
    }

    @Override
    public Set<SalverNode> getSalvers(String table, PartitionCycle cycle) {
        return getSalvers(table, cycle.getFloor());
    }

    @Override
    public Set<SalverNode> getSalvers(String table, int shardId) {
        return salverNodes.values().stream().filter(salverNode -> salverNode.contains(table, shardId)).collect(Collectors.toSet());
    }

    @Override
    public Set<SalverNode> getSalvers(String table, long unixTime) {
        return salverNodes.values().stream().filter(salverNode -> salverNode.contains(table, unixTime)).collect(Collectors.toSet());
    }

    @Override
    public SalverNode getSalver(NodeBaseEntry nodeBaseEntry) {
        return salverNodes.get(nodeBaseEntry);
    }

    @Override
    public SalverNode getSalver(String table, PartitionCycle cycle, int shardId) {
        return getSalver(table, cycle.getFloor(), shardId);
    }

    @Override
    public SalverNode getSalver(String table, long unixTime, int shardId) {
        List<SalverNode> list = salverNodes.values().stream().filter(salverNode -> salverNode.contains(table, shardId, unixTime)).collect(Collectors.toList());
        assert list.size() == 1;
        return list.get(0);
    }

    @Override
    public Set<SalverNode> getIdleSalvers() {
        return salverNodes.values().stream().filter(SalverNode::isIdle).collect(Collectors.toSet());
    }

    @Override
    public int getShardNum(String table) {
        return salverNodes.values().stream().mapToInt(node -> node.getShardIdList(table).size()).sum();
    }

    @Override
    public void pull(String table, int partitionNum) {
        for (int i = 0; i < partitionNum; i++) {
            masterStrategy.createTask(PullTask.newInstance(KafkaPullContext.newInstance(table, i)));
        }
    }

    @Override
    public void stopPull(String table, int partitionNum) {
        for (int i = 0; i < partitionNum; i++) {
            masterStrategy.stopTask(PullTask.newInstance(KafkaPullContext.newInstance(table, i)));
        }
    }

    @Override
    public void pullShard(String table, int shardId) {
        masterStrategy.createTask(PullTask.newInstance(KafkaPullContext.newInstance(table, shardId)));
    }

    @Override
    public void stopPullShard(String table, int shardId) {
        masterStrategy.stopTask(PullTask.newInstance(KafkaPullContext.newInstance(table, shardId)));
    }

    @Override
    public void pullFromEnd(String table, int partitionNum) {
        for (int i = 0; i < partitionNum; i++) {
            masterStrategy.createTask(PullTask.newInstance(KafkaPullContext.newEndInstance(table, i)));
        }
    }
}
