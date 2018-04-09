package com.huya.search.tasks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.index.DistributedEngine;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.opeation.DisOperationFactory;
import com.huya.search.settings.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/25.
 */
@Singleton
public class RealMasterTasksService extends MasterTasksService {

    private static final Logger LOG = LoggerFactory.getLogger(RealMasterTasksService.class);

    private Settings settings;

    private DistributedEngine engine;

    private MonitorMeta monitorMeta;

    @Inject
    public RealMasterTasksService(@Named("Tasks") Settings settings, DistributedEngine engine,
                                  @Named("MasterMonitorMeta") MonitorMeta monitorMeta) {
        this.settings = settings;
        this.engine = engine;
        this.monitorMeta = monitorMeta;
    }


    /**
     * Master Tasks Service 启动
     * 主要是将分布式引擎启动，分布式引擎将会关注
     * 节点的加入、丢失，节点的任务分配、转移等事项
     * @throws SearchException 索引服务异常
     */
    @Override
    protected void doStart() throws SearchException {
        engine.start();

        LOG.info("master tasks service start success");
    }


    @Override
    public DistributedEngine getDistributedEngine() {
        return engine;
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        engine.close();

        LOG.info("distributed engine close");
    }

    @Override
    public void taskStart(String table) {
        LOG.info("take the initiative to start {} task", table);

        engine.put(DisOperationFactory.getIndexContext(table));

        LOG.info("{} task start finish", table);
    }

    /**
     * 主动开启任务
     */
    @Override
    public void tasksStart() {

        LOG.info("take the initiative to start tasks");

        monitorMeta.iterator().forEachRemaining(
                metaDefine -> {
                    if (metaDefine.isOpen()) {
                        engine.put(DisOperationFactory.getIndexContext(metaDefine.getTable()));
                    }
                }
        );

        LOG.info("start finish");
    }

    @Override
    public void taskShutdown(String table) {
        LOG.info("take the initiative to shutdown {} task", table);

        engine.remove(DisOperationFactory.getIndexContext(table));

        LOG.info("{} task shut down finish", table);
    }

    /**
     * 主动关闭任务
     */
    public void tasksShutdown() {

        LOG.info("take the initiative to shut down tasks");

        monitorMeta.iterator().forEachRemaining(
                metaDefine -> {
                    if (metaDefine.isOpen()) {
                        engine.remove(DisOperationFactory.getIndexContext(metaDefine.getTable()));
                    }
                }
        );

        LOG.info("shut down finish");
    }

    @Override
    public String getName() {
        return "RealMastTasksService";
    }


}
