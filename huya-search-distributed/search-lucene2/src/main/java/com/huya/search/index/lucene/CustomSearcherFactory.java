package com.huya.search.index.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/25.
 */
public interface CustomSearcherFactory<T extends IndexSearcher> {

    T newSearcher(IndexReader reader, IndexReader previousReader) throws IOException;
}
