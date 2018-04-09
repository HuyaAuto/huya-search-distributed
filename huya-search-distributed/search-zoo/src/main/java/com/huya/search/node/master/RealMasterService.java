package com.huya.search.node.master;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.index.block.DisDataBlockManager;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.rpc.RpcService;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.huya.search.node.NodePath.MASTER;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
@Singleton
public class RealMasterService extends MasterService {

    private MasterLatch masterLatch;

    private RpcService rpcService;

    @Inject
    public RealMasterService(ZooKeeperOperator operator, RpcService rpcService) {
        this.masterLatch = new MasterLatch(operator);
        this.rpcService = rpcService.sync(this.masterLatch);
    }

    @Override
    protected void doStart() throws SearchException {
        try {
            //开始选举
            masterLatch.start();

            //完全确定 RPC 服务的功能（MASTER 与 SALVER 的 RPC 功能并不一样）
            rpcService.start();
        } catch (Exception e) {
            throw new SearchException("master service start error", e);
        }
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        try {
            //先关闭选举
            masterLatch.close();

            //在关闭 RPC 服务
            rpcService.close();
        } catch (Exception e) {
            throw new SearchException("master service close error", e);
        }
    }

    @Override
    public String getName() {
        return "RealMasterService";
    }

    @Override
    public void quitMaster() throws IOException {
        masterLatch.close();
    }

    @Override
    public boolean isMaster() {
        return masterLatch.isMaster();
    }

    @Override
    public NodeBaseEntry getMaster() throws Exception {
        return masterLatch.getMaster();
    }

    @Override
    public void serverUp() {
        rpcService.serverUp();
    }

    public class MasterLatch implements Closeable {

        private String serviceUrl;

        private final LeaderLatch leaderLatch;

        private NodeBaseEntry masterNodeBaseEntry;

        private volatile boolean isMaster = false;

        MasterLatch(ZooKeeperOperator operator) {
            this.serviceUrl = operator.getServiceUrl();
            this.leaderLatch = new LeaderLatch(operator.getClient(), MASTER, this.serviceUrl);
            this.leaderLatch.addListener(new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    isMaster = true;
                }

                @Override
                public void notLeader() {
                    isMaster = false;
                }
            });
        }

        public void start() throws Exception {
            leaderLatch.start();
        }

        public void close() throws IOException {
            leaderLatch.close();
        }

        public boolean isMaster() {
            return isMaster;
        }

        public NodeBaseEntry getMaster() throws Exception {
            if (masterNodeBaseEntry == null) {
                for (; ; ) {
                    if (leaderLatch.hasLeadership()) {
                        masterNodeBaseEntry = DisDataBlockManager.NodeBaseEntryImpl.newInstance(leaderLatch.getLeader().getId());
                        break;
                    } else {
                        TimeUnit.SECONDS.sleep(5);
                    }
                }
            }

            return masterNodeBaseEntry;
        }

    }
}
