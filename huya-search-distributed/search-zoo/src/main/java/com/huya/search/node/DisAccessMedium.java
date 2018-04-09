package com.huya.search.node;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.DisRefreshContext;
import org.apache.avro.AvroRemoteException;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/9.
 */
public interface DisAccessMedium {

    void createMeta(String metaJson) throws Exception;

    void removeMeta(String table) throws Exception;

    void updateMeta(String table, String content) throws Exception;

    void openMeta(String table) throws Exception;

    void closeMeta(String table) throws Exception;

    String metas() throws Exception;

    String lastMetas() throws Exception;

    String meta(String table) throws Exception;

    String lastMeta(String table) throws Exception;

    void refresh(DisRefreshContext refreshContext) throws Exception;

    QueryResult<? extends Iterable<IndexableField>> sql(String sql) throws Exception;

    String insertStat() throws AvroRemoteException;
}
