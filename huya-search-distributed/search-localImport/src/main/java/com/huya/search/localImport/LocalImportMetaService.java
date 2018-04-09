package com.huya.search.localImport;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.index.meta.monitor.MonitorMeta;
import com.huya.search.index.meta.monitor.MonitorOriginalMeta;
import com.huya.search.service.AbstractOrderService;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
@Singleton
public class LocalImportMetaService extends AbstractOrderService {

    private MonitorOriginalMeta monitorOriginalMeta;

    private MonitorMeta monitorMeta;

    @Inject
    public LocalImportMetaService(MonitorMeta monitorMeta,  @Named("MySQLMeta") MonitorOriginalMeta monitorOriginalMeta) {
        this.monitorMeta = monitorMeta;
        this.monitorOriginalMeta = monitorOriginalMeta;
    }

    @Override
    protected void doStart() throws SearchException {
        monitorOriginalMeta.bind(monitorMeta);
        monitorOriginalMeta.load();
    }

    @Override
    protected void doStop() throws SearchException {

    }

    @Override
    protected void doClose() throws SearchException {
        monitorOriginalMeta.unBind(monitorMeta);
        monitorOriginalMeta.unload();
    }

    @Override
    public String getName() {
        return "LocalImportMetaService";
    }
}
