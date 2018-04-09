package com.huya.search.node;

import com.huya.search.SearchException;
import com.huya.search.service.AbstractLifecycleService;
import com.huya.search.util.JsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.huya.search.node.NodePath.SALVERS;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public abstract class TaskDistributionService extends AbstractLifecycleService implements Cluster, TaskManager {

    protected static final Logger LOG = LoggerFactory.getLogger(TaskDistributionService.class);

    private ZooKeeperOperator zo;

    private PathChildrenCache pathChildrenCache;

    TaskDistributionService(ZooKeeperOperator zo) {
        this.zo = zo;
    }

    @Override
    protected void doStart() throws SearchException {
        //0. 同步节点
        syncSalvers();
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        try {
            pathChildrenCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void syncSalvers() {
        CuratorFramework client = zo.getClient();
        pathChildrenCache = new PathChildrenCache(client, SALVERS, true);
        pathChildrenCache.getListenable().addListener((aClient, event) -> {
            PathChildrenCacheEvent.Type type = event.getType();
            if (type == CHILD_ADDED || type == CHILD_REMOVED) {
                NodeBaseEntry nodeBaseEntry = JsonUtil.getObjectMapper().readValue(event.getData().getData(), NodeBaseEntryImpl.class);
                switch (event.getType()) {
                    case CHILD_ADDED:
                        addSalver(nodeBaseEntry);
                        break;
                    case CHILD_REMOVED:
                        lostSalver(nodeBaseEntry);
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

}
