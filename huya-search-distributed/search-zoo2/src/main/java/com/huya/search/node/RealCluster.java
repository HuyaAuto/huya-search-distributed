package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
@Singleton
public class RealCluster implements Cluster {

    private volatile Map<NodeBaseEntry, SalverNode> salverNodes = new ConcurrentHashMap<>();

    private SalverNodeFactory salverNodeFactory;

    private TasksBox tasksBox;

    @Inject
    public RealCluster(SalverNodeFactory salverNodeFactory, TasksBox tasksBox) {
        this.salverNodeFactory = salverNodeFactory;
        this.tasksBox = tasksBox;
    }

    @Override
    public Set<SalverNode> getAllSalvers() {
        return new HashSet<>(salverNodes.values());
    }

    @Override
    public Set<SalverNode> getSalvers(String table) {
        return tasksBox.getSalverNodesQueryInfo(table).getNodeSet();
    }

    @Override
    public SalverNode getSalverNode(NodeBaseEntry nodeBaseEntry) {
        return salverNodes.get(nodeBaseEntry);
    }

    @Override
    public SalverNodesQueryInfo getSalverNodesQueryInfo(String table) {
        return tasksBox.getSalverNodesQueryInfo(table);
    }

    @Override
    public void addSalver(NodeBaseEntry nodeBaseEntry) {
        SalverNode salverNode = salverNodes.get(nodeBaseEntry);
        if (salverNode == null) {
            salverNodes.put(nodeBaseEntry, salverNodeFactory.create(nodeBaseEntry));
        } else {
            salverNode.asJoin();
        }
    }

    @Override
    public void lostSalver(NodeBaseEntry nodeBaseEntry) {
        SalverNode salverNode = salverNodes.get(nodeBaseEntry);
        assert salverNode != null;
        salverNode.asLost();
    }

    @Override
    public List<Integer> getShardIdList(SalverNode salverNode, String table) {
        return tasksBox.getShardIdList(salverNode, table);
    }

}
