package com.huya.search.meta.monitor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.node.ZooKeeperOperator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.EnsurePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

import static com.huya.search.node.NodePath.META;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/24.
 *
 * 子节点监控 Zookeeper 元数据的变化，将会推送【改变】给绑定了自己的 MonitorMeta
 */
@Singleton
public class ZooKeeperMonitorMeta extends MonitorMeta {

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperMonitorMeta.class);

    private ZooKeeperOperator zo;

    private MonitorMeta monitorMeta;

    private PathChildrenCache pathChildrenCache;

    @Inject
    public ZooKeeperMonitorMeta(ZooKeeperOperator zo, @Named("MetaFollowerIt") MonitorMeta monitorMeta) {
        this.zo = zo;
        this.monitorMeta = monitorMeta;
    }

    public void start() {
        bind(monitorMeta);
        CuratorFramework client = zo.getClient();
        try {
            new EnsurePath(META).ensure(client.getZookeeperClient());
        } catch (Exception e) {
            LOG.error("ensure mate path error", e);
        }
        syncTables();
    }

    public void stop() {
        try {
            pathChildrenCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unBind(monitorMeta);
    }

    private void syncTables() {
        CuratorFramework client = zo.getClient();
        pathChildrenCache = new PathChildrenCache(client, META, true);
        pathChildrenCache.getListenable().addListener((aClient, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    addTable(event.getData()); break;
                case CHILD_REMOVED:
                    removeTable(event.getData()); break;
                case CHILD_UPDATED:
                    updateTable(event.getData()); break;
            }
        });
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            LOG.error("META pathChildrenCache error", e);
        }
    }

    private String tableName(String path) {
        return path.substring(META.length() + 1);
    }

    private void addTable(ChildData childData) {
        LOG.info("addTable: " + childData.getPath() + " to zookeeper");
        this.add(JsonMetaUtil.createTimeMetaDefineFromBytes(childData.getData()));
    }

    private void removeTable(ChildData childData) {
        LOG.info("removeTable: " + childData.getPath() + " from zookeeper");
        this.remove(tableName(childData.getPath()));
    }

    private void updateTable(ChildData childData) {
        LOG.info("updateTable: " + childData.getPath() + " to zookeeper");
        addTable(childData);
    }

    @Override
    public String tag() {
        return "ZooKeeperMonitorMeta";
    }

    @Override
    protected void addSelf(TimelineMetaDefine metaDefine) {
        //do nothing
    }

    @Override
    protected void updateSelf(String table, MetaDefine metaDefine) {
        //do nothing
    }

    @Override
    protected void updateSelf(TimelineMetaDefine metaDefine) {
        //do nothing
    }

    @Override
    protected void removeSelf(String table) {
        //do nothing
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        return null;
    }

    @Override
    public TimelineMetaDefine getTimelineMetaDefine(String table) {
        return null;
    }
}
