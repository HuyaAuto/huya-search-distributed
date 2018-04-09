package com.huya.search.meta;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.meta.monitor.ZooKeeperMonitorMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
@Singleton
public class RealSalversMetaService extends SalversMetaService {

    private static final Logger LOG = LoggerFactory.getLogger(RealSalversMetaService.class);

    private ZooKeeperMonitorMeta monitorMeta;

    @Inject
    public RealSalversMetaService(ZooKeeperMonitorMeta monitorMeta) {
        this.monitorMeta = monitorMeta;
    }

    @Override
    protected void doStart() throws SearchException {
        monitorMeta.start();
        LOG.info("salvers started to listen meta change");
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        monitorMeta.stop();
        LOG.info("salvers stopped to listen meta change");
    }

    @Override
    public String getName() {
        return "RealSalversMetaService";
    }

}
