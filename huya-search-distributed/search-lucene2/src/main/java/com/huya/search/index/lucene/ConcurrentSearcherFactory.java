package com.huya.search.index.lucene;

import com.huya.search.util.ThreadPoolSingleton;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/23.
 */
public class ConcurrentSearcherFactory extends SearcherFactory {

    private ConcurrentSearcherFactory() {
    }

    private static class ConcurrentSearcherFactoryHandle {
        private final static ConcurrentSearcherFactory INSTANCE = new ConcurrentSearcherFactory();
    }

    public static ConcurrentSearcherFactory getInstance() {
        return ConcurrentSearcherFactoryHandle.INSTANCE;
    }

    @Override
    public IndexSearcher newSearcher(IndexReader reader, IndexReader previousReader) {
        return new IndexSearcher(reader, getExecutorService());
    }

    public ExecutorService getExecutorService() {
        //todo 实现一个动态的线程池
        return ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);
    }
}
