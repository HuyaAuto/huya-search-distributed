package com.huya.search.index.lucene;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public interface LazyLuceneOperator {

    WriterAndReadLuceneOperator lazyGet() throws Exception;
}
