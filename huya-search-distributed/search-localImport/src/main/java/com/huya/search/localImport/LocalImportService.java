package com.huya.search.localImport;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.Engine;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.data.SearchDataRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
@Singleton
public class LocalImportService {

    private static final Logger LOG = LoggerFactory.getLogger(LocalImportService.class);

    private Engine engine;

    @Inject
    public LocalImportService(Engine engine) {
        this.engine = engine;
    }

    public void start(LogSource logSource) {
        String table = logSource.getTable();
        while (logSource.hasNext()) {
            SearchDataRow searchDataRow = logSource.next();
            if (searchDataRow == null) continue;
            try {
                engine.put(table, searchDataRow);
            } catch (ShardsOperatorException e) {
                LOG.error("local import engine put error", e);
            }
        }
        LOG.info("--------------import finish--------------");
    }
}
