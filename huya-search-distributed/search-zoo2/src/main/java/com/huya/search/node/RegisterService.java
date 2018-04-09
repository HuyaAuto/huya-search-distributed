package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.util.JsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
@Singleton
public class RegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterService.class);

    private static final long UN_REGISTER_UNIX_TIME = -1L;

    private ZooKeeperOperator zo;

    private long registerUnixTime = UN_REGISTER_UNIX_TIME;

    @Inject
    public RegisterService(ZooKeeperOperator zo) {
        this.zo = zo;
    }

    public void registerNode() {
        NodeBaseEntry nodeBaseEntry = zo.getNodeBaseEntry();
        String serviceUrl = nodeBaseEntry.getServiceUrl();
        try {
            byte[] bytes = JsonUtil.getObjectMapper().writeValueAsBytes(nodeBaseEntry);
            zo.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .withACL(OPEN_ACL_UNSAFE).forPath(NodePath.salverPath(serviceUrl), bytes);
            registerUnixTime = System.currentTimeMillis();
            LOG.info("register self salver {}", serviceUrl);
        } catch (Exception e) {
            LOG.error("register salvers {} error", serviceUrl, e);
        }
    }

    public boolean hasRegisterNode() {
        return registerUnixTime != UN_REGISTER_UNIX_TIME;
    }

    public void unRegisterNode() {
        CuratorFramework client = zo.getClient();
        String serviceUrl = zo.getServiceUrl();
        try {
            client.delete().forPath(NodePath.salverPath(serviceUrl));
            LOG.info("unRegister self salver {}", serviceUrl);
        } catch (Exception e) {
            LOG.error("unRegister salvers " + serviceUrl + " error", e);
        }
    }
}
