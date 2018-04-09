package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.service.AbstractOrderService;
import com.huya.search.settings.Settings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;


/**
 * Created by zhangyiqun1@yy.com on 2017/10/17.
 */
@Singleton
public class ZooKeeperOperator extends AbstractOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperOperator.class);

    private NodeEntry nodeEntry;

    private CuratorFramework client;

    private volatile ConnectionState state;

    private List<Runnable> connectionRunnable = new ArrayList<>();

    private List<Runnable> disConnectionRunnable = new ArrayList<>();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public ZooKeeperOperator(@Named("Node")Settings settings, NodeEntry nodeEntry) {
        String zkHostPort = settings.get("zkHostPort");
        int    zkTimeout  = settings.getAsInt("zkTimeout", 15000);

        this.nodeEntry    = nodeEntry;
        client = CuratorFrameworkFactory.newClient(zkHostPort, new ExponentialBackoffRetry(zkTimeout, 3));
    }

    public NodeBaseEntry getNodeBaseEntry() {
        return nodeEntry;
    }

    @Override
    protected void doStart() throws SearchException {
        client.getConnectionStateListenable().addListener((client1, newState) -> {
            this.state = newState;
            if (newState.isConnected()) {
                doConnect();
            }
            else {
                doDisConnect();
            }
        });

        client.start();
        client.setACL().withACL(OPEN_ACL_UNSAFE);
        try {
            client.blockUntilConnected();
        } catch (InterruptedException e) {
            throw new SearchException("zookeeper connect error", e);
        }
        LOG.info("zookeeper operator connected: " + nodeEntry.getServiceHost());
    }

    private void doDisConnect() {
        disConnectionRunnable.forEach(runnable -> executor.submit(runnable));
    }

    private void doConnect() {
        connectionRunnable.forEach(runnable -> executor.submit(runnable));
    }

    public void addConnectRunnable(Runnable runnable) {
        if (state != null) {
            if (state.isConnected()) {
                executor.submit(runnable);
            }
        }
        connectionRunnable.add(runnable);
    }

    public void addDisConnectRunnable(Runnable runnable) {
        if (state != null) {
            if (!state.isConnected()) {
                executor.submit(runnable);
            }
        }
        disConnectionRunnable.add(runnable);
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        client.close();
        LOG.info("zookeeper operator connect close: " + nodeEntry.getServiceHost());
    }

    public CuratorFramework getClient() {
        return client;
    }

    public String getServiceIP() {
        return nodeEntry.getServiceHost();
    }

    public int getServicePort() {
        return nodeEntry.getServicePort();
    }

    public String getServiceUrl() {
        return nodeEntry.getServiceUrl();
    }

    @Override
    public String getName() {
        return "ZooKeeperOperator";
    }

//
////    public static void main(String[] args) throws Exception {
////        ModulesBuilder modules = ModulesBuilder.getInstance();
////        modules.add(new AbstractModule() {
////            @Override
////            protected void configure() {
////                bind(Settings.class).annotatedWith(Names.named("Node")).to(NodeSettings.class).in(Singleton.class);
////                bind(NodeEntry.class).toProvider(() -> new NodeEntry() {
////                    @Override
////                    public int getExpectedClusterSize() {
////                        return 1;
////                    }
////
////                    @Override
////                    public long getStartTimestamp() {
////                        return 0;
////                    }
////
////                    @Override
////                    public String getServiceHost() {
////                        return IpUtils.getHostName();
////                    }
////
////                    @Override
////                    public int getServicePort() {
////                        return 28889;
////                    }
////
////                    @Override
////                    public String getServiceUrl() {
////                        return getServiceHost() + ":" + getServicePort();
////                    }
////                });
////            }
////        });
////        ZooKeeperOperator zooKeeperOperator = modules.createInjector().getInstance(ZooKeeperOperator.class);
////        zooKeeperOperator.start();
////
////        String path = "/huya-search/lucene/lucene-dis-test/";
////
////        try {
////
////            for (int i = 0; i < 4; i++) {
////                zooKeeperOperator.client.delete().deletingChildrenIfNeeded().forPath(path + "test/" + i);
////            }
////
//////            for (int i = 0; i < 5; i++) {
//////                zooKeeperOperator.client.delete().deletingChildrenIfNeeded().forPath(path + "live_server/" + i);
//////            }
////        } catch (Exception e) {
////            LOG.error("delete error", e);
////        }
////
////
////    }

}
