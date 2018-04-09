package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.huya.search.node.NodePath.MASTER;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
@Singleton
public class MasterLatch implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(MasterLatch.class);

    private final LeaderLatch leaderLatch;

    private NodeBaseEntry masterNodeBaseEntry;

    private volatile boolean isMaster = false;

    @Inject
    public MasterLatch(ZooKeeperOperator operator) {
        this.leaderLatch = new LeaderLatch(operator.getClient(), MASTER, operator.getServiceUrl());
        this.leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                isMaster = true;
                masterNodeBaseEntry = operator.getNodeBaseEntry();
            }

            @Override
            public void notLeader() {
                isMaster = false;
            }
        });
    }

    public void start() throws Exception {
        leaderLatch.start();

        while (masterNodeBaseEntry == null) {
            try {
                Participant participant = leaderLatch.getLeader();
                if (participant != null) {
                    masterNodeBaseEntry = NodeBaseEntryImpl.newInstance(participant.getId());
                }
            } catch (Exception ignore) {}
            leaderLatch.await(1, TimeUnit.SECONDS);
        }
        LOG.info(isMaster + " start current");
    }

    public void close() throws IOException {
        leaderLatch.close();
    }

    public boolean isMaster() {
        LOG.info(isMaster + " current");
        return isMaster;
    }

    public NodeBaseEntry getMaster() {
        return masterNodeBaseEntry;
    }


}
