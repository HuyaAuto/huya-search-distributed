package com.huya.search;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.opeation.RefreshContext;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/9.
 */
public interface AccessMedium {

    void createMeta(String metaJson);

    void removeMeta(String table);

    void updateMeta(String metaJson);

    void openMeta(String table);

    void closeMeta(String table);

    String viewMeta(String table);

    void insert(String table, SearchData<SearchDataRow> searchData);

    void refresh(RefreshContext refreshContext);

    QueryResult<? extends Iterable<IndexableField>> sql(String sql);
}
