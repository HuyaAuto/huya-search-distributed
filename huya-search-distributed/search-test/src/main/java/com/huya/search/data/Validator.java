package com.huya.search.data;

import com.huya.search.index.data.SearchData;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/11.
 */
public interface Validator {

    void injectData(String table, SearchData<? extends Iterable<IndexableField>> searchData);

    SearchData<? extends Iterable<IndexableField>> query(String sql);

    void remove(String table);
}
