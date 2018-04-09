package com.huya.search.index.strategy;

import com.huya.search.hash.ConsistentHash;
import com.huya.search.hash.Node;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.salvers.SalverNode;
import com.huya.search.tasks.PullTask;
import com.huya.search.util.WarnService;
import org.apache.avro.AvroRemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/13.
 */
public abstract class NodeConsistentHash extends ConsistentHash<SalverNode, PullTask> {

    private static final Logger LOG = LoggerFactory.getLogger(NodeConsistentHash.class);

    NodeConsistentHash(int virtualNodeNum) {
        super(virtualNodeNum);
    }

    @Override
    public void joinSalverHandOver(Node providerNode, Node acquirerNode, Set<PullTask> handOverSet) {
        assert providerNode instanceof SalverNode;
        assert acquirerNode instanceof SalverNode;

        SalverNode realProviderNode = (SalverNode) providerNode;
        SalverNode realAcquirerNode = (SalverNode) acquirerNode;


        LOG.info("has a new salver join, {} handOver to {}", realProviderNode.getNodeEntry().getServiceUrl(), realAcquirerNode.getNodeEntry().getServiceUrl());

        handOverSet.forEach(PullTask -> LOG.info(PullTask.toString()));

        handOverSet.forEach(assignObject -> {
            assert assignObject != null;
            providerNodeClosePullTask(realProviderNode, assignObject);
            LOG.info(assignObject.toString());
            acquirerNodePullTask(realAcquirerNode, assignObject);
        });
    }

    @Override
    public void lostSalverHandOver(Node lostNode, Node acquirerNode, Set<PullTask> handOverSet) {
        assert lostNode instanceof SalverNode;
        assert acquirerNode instanceof SalverNode;

        SalverNode realLostNode     = (SalverNode) lostNode;
        SalverNode realAcquirerNode = (SalverNode) acquirerNode;

        LOG.info("has a salver lost, {} handOver to {}", realLostNode.getNodeEntry().getServiceUrl(), realAcquirerNode.getNodeEntry().getServiceUrl());

        handOverSet.forEach(pullContext -> LOG.info(pullContext.toString()));

        handOverSet.forEach(assignObject -> {
            assert assignObject != null;
            acquirerNodePullTask(realAcquirerNode, assignObject);
        });
    }

    private void providerNodeClosePullTask(SalverNode realProviderNode, PullTask pullTask) {
        try {
            masterStrategyClosePullTask(realProviderNode.getNodeEntry(), pullTask);
        } catch (AvroRemoteException e) {
            LOG.error("provider node " + realProviderNode.getNodeEntry().getServiceUrl() + " close pull " + pullTask + " task error", e);
            WarnService.getInstance().send("provider node " + realProviderNode.getNodeEntry().getServiceUrl() + " close pull " + pullTask + " task error", e);
        }
    }

    private void acquirerNodePullTask(SalverNode realAcquirerNode, PullTask pullTask) {
        try {
            masterStrategyPullTask(realAcquirerNode.getNodeEntry(), pullTask);
        } catch (AvroRemoteException e) {
            LOG.error("provider node " + realAcquirerNode.getNodeEntry().getServiceUrl() + " open pull " + pullTask + " task error", e);
            WarnService.getInstance().send("provider node " + realAcquirerNode.getNodeEntry().getServiceUrl() + " open pull " + pullTask + " task error", e);
        }
    }

    @Override
    public void assign(Node acquirerNode, PullTask assignObject) {
        assert acquirerNode instanceof SalverNode;

        SalverNode realAcquirerNode = (SalverNode) acquirerNode;

        LOG.info("assign {} to {}", assignObject, realAcquirerNode.getNodeEntry().getServiceUrl());

        acquirerNodePullTask((SalverNode)acquirerNode, assignObject);
    }

    @Override
    public void discharge(Node acquirerNode, PullTask assignObject) {
        assert acquirerNode instanceof SalverNode;

        SalverNode realAcquirerNode = (SalverNode) acquirerNode;

        LOG.info("discharge {} from {}", assignObject, realAcquirerNode.getNodeEntry().getServiceUrl());

        providerNodeClosePullTask((SalverNode)acquirerNode, assignObject);
    }

    public abstract void masterStrategyPullTask(NodeBaseEntry nodeBaseEntry, PullTask pullTask) throws AvroRemoteException;

    public abstract void masterStrategyClosePullTask(NodeBaseEntry nodeBaseEntry, PullTask pullTask) throws AvroRemoteException;
}
