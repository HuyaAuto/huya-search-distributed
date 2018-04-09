package com.huya.search.index.lucene;

import com.huya.search.util.ThreadPoolSingleton;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/25.
 */
public class HuyaCustomSearcherFactory implements CustomSearcherFactory<CustomIndexSearcher> {

    private HuyaCustomSearcherFactory() {
    }

    private static class HuyaCustomSearcherFactoryHandle {
        private final static HuyaCustomSearcherFactory INSTANCE = new HuyaCustomSearcherFactory();
    }

    public static CustomSearcherFactory<CustomIndexSearcher> getInstance() {
        return HuyaCustomSearcherFactoryHandle.INSTANCE;
    }


    @Override
    public CustomIndexSearcher newSearcher(IndexReader reader, IndexReader previousReader) throws IOException {
        return new CustomIndexSearcher(reader, getExecutorService());
    }

    public ExecutorService getExecutorService() {
        //todo 实现有个动态的线程池
        return ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);
    }
}
