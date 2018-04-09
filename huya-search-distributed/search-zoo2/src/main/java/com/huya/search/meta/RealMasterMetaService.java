package com.huya.search.meta;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.MonitorOriginalMeta;
import com.huya.search.meta.monitor.ZooKeeperMasterMonitorMeta;

import java.util.Iterator;


/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
@Singleton
public class RealMasterMetaService extends MasterMetaService {

    private ZooKeeperMasterMonitorMeta zmmm;

    private MonitorOriginalMeta monitorOriginalMeta;

    @Inject
    public RealMasterMetaService(ZooKeeperMasterMonitorMeta zmmm, @Named("MySQLMeta") MonitorOriginalMeta monitorOriginalMeta) {
        this.zmmm = zmmm;
        this.monitorOriginalMeta = monitorOriginalMeta;
    }


    @Override
    protected void doStart() throws SearchException {
        monitorOriginalMeta.bind(zmmm);
        monitorOriginalMeta.load();
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        monitorOriginalMeta.unBind(zmmm);
        monitorOriginalMeta.unload();
    }

    @Override
    public String getName() {
        return "RealMasterMetaService";
    }

    @Override
    public void createMeta(TimelineMetaDefine metaDefine) {
        monitorOriginalMeta.add(metaDefine);
    }

    @Override
    public void removeMeta(String table) {
        monitorOriginalMeta.remove(table);
    }

    @Override
    public void updateMeta(String table, MetaDefine metaDefine) {
        monitorOriginalMeta.update(table, metaDefine);
    }

    //补充元数据开关方法

    @Override
    public void openMeta(String table) {

    }

    @Override
    public void closeMeta(String table) {

    }

    @Override
    public Iterator<TimelineMetaDefine> metas() {
        return zmmm.iterator();
    }

    @Override
    public TimelineMetaDefine meta(String table) {
        return zmmm.getTimelineMetaDefine(table);
    }
}
