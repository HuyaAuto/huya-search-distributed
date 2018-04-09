package com.huya.search.node.salvers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.meta.SalversMetaService;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.NodePath;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.util.JsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
@Singleton
public class RealSalversService extends SalversService {

    private static final Logger LOG = LoggerFactory.getLogger(RealSalversService.class);

    private ZooKeeperOperator zo;

    private SalversMetaService salversMetaService;

    private long registerUnixTime = -1L;

    @Inject
    public RealSalversService(ZooKeeperOperator zo, SalversMetaService salversMetaService) {
        this.zo = zo;
        this.salversMetaService = salversMetaService;
    }

    private void registerSalvers() {
        zo.addConnectRunnable(this::doRegisterSalvers);
        zo.addDisConnectRunnable(this::unRegisterSalvers);
    }

    private void doRegisterSalvers() {
        NodeBaseEntry nodeBaseEntry = zo.getNodeBaseEntry();
        String serviceUrl = nodeBaseEntry.getServiceUrl();
        try {
            byte[] bytes = JsonUtil.getObjectMapper().writeValueAsBytes(nodeBaseEntry);
            zo.getClient().create().withMode(CreateMode.EPHEMERAL)
                    .withACL(OPEN_ACL_UNSAFE).forPath(NodePath.salverPath(serviceUrl), bytes);
            registerUnixTime = System.currentTimeMillis();
            LOG.error("register self salver {}", serviceUrl);
        } catch (Exception e) {
            LOG.error("register salvers {} error", serviceUrl, e);
        }
    }

    private void unRegisterSalvers() {
        CuratorFramework client = zo.getClient();
        String serviceUrl = zo.getServiceUrl();
        try {
            client.delete().forPath(NodePath.salverPath(serviceUrl));
            LOG.error("unRegister self salver {}", serviceUrl);
        } catch (Exception e) {
            LOG.error("unRegister salvers " + serviceUrl + " error", e);
        }
    }


    @Override
    protected void doStart() throws SearchException {
        registerSalvers();
        salversMetaService.start();
        LOG.info(zo.getServiceUrl() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        salversMetaService.close();
        LOG.info(zo.getServiceUrl() + " stopped");
    }

    @Override
    public String getName() {
        return "RealSalversService";
    }

    @Override
    long getRegisterUnixTime() {
        return registerUnixTime;
    }
}
