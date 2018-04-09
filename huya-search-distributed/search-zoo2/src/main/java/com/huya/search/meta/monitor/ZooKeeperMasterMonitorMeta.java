package com.huya.search.meta.monitor;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.node.NodePath;
import com.huya.search.node.ZooKeeperOperator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.huya.search.node.NodePath.META;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/20.
 */
@Singleton
public class ZooKeeperMasterMonitorMeta extends MonitorMeta {

    private ZooKeeperOperator zo;

    @Inject
    public ZooKeeperMasterMonitorMeta(ZooKeeperOperator zo) {
        this.zo = zo;
    }

    @Override
    public String tag() {
        return "ZooKeeperMasterMonitorMeta";
    }

    @Override
    protected void addSelf(TimelineMetaDefine metaDefine) {
        updateSelf(metaDefine);
    }

    @Override
    protected void updateSelf(String table, MetaDefine metaDefine) {
        TimelineMetaDefine oldMetaDefine = getTimelineMetaDefine(table);
        assert oldMetaDefine != null;
        oldMetaDefine.add(metaDefine);
        updateSelf(oldMetaDefine);
    }

    @Override
    protected void updateSelf(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        String tablePath = NodePath.tablePath(table);
        String metaDefineJson = metaDefine.toObject().toString();
        try {
            CuratorFramework client = zo.getClient();
            Stat stat = client.checkExists().forPath(tablePath);
            if (stat == null) {
                client.create().withMode(CreateMode.PERSISTENT).withACL(OPEN_ACL_UNSAFE).forPath(tablePath, metaDefineJson.getBytes());
            }
            else {
                client.setData().forPath(tablePath, metaDefineJson.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void removeSelf(String table) {
        String tablePath = NodePath.tablePath(table);
        try {
            CuratorFramework client = zo.getClient();
            Stat stat = client.checkExists().forPath(tablePath);
            if (stat != null) {
                client.delete().forPath(tablePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        Map<String, TimelineMetaDefine> map = Maps.newTreeMap();
        try {
            CuratorFramework client = zo.getClient();
            List<String> tables = client.getChildren().forPath(META);
            tables.forEach(table -> {
                try {
                    byte[] bytes = client.getData().forPath(NodePath.tablePath(table));
                    map.put(table, JsonMetaUtil.createTimeMetaDefineFromBytes(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.values().iterator();
    }

    @Override
    public TimelineMetaDefine getTimelineMetaDefine(String table) {
        try {
            CuratorFramework client = zo.getClient();
            byte[] bytes = client.getData().forPath(NodePath.tablePath(table));
            return JsonMetaUtil.createTimeMetaDefineFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
