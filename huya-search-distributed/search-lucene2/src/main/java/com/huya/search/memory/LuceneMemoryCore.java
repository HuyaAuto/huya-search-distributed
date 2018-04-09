package com.huya.search.memory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.lucene.WriterAndReadLuceneOperator;
import com.huya.search.index.meta.ShardMetaDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/22.
 */
@Singleton
public class LuceneMemoryCore implements MemoryCore<WriterAndReadLuceneOperator> {

    public static final Logger LOG = LoggerFactory.getLogger(LuceneMemoryCore.class);

    private Map<ShardMetaDefine, WriterAndReadLuceneOperator> memoryMap = new ConcurrentHashMap<>();

    @Inject
    public LuceneMemoryCore() {}

    @Override
    public void add(WriterAndReadLuceneOperator luceneOperator, ShardMetaDefine metaDefine) {
        memoryMap.put(metaDefine, luceneOperator);
    }

    @Override
    public WriterAndReadLuceneOperator get(ShardMetaDefine metaDefine) {
        return memoryMap.get(metaDefine);
    }

    @Override
    public WriterAndReadLuceneOperator remove(ShardMetaDefine metaDefine) {
        return memoryMap.remove(metaDefine);
    }

    @Override
    public void strategy() {

    }

    @Override
    public void close() {
        for (Map.Entry<ShardMetaDefine, WriterAndReadLuceneOperator> entry : memoryMap.entrySet()) {
            WriterAndReadLuceneOperator operator = entry.getValue();
            try {
                if (operator.isLoadReader()) {
                    operator.closeReader();
                }
            } catch (IOException e) {
                LOG.error("close reader error", e);
            }
            try {
                if (operator.isLoadWriter()) {
                    operator.closeWriter();
                }
            } catch (IOException e) {
                LOG.error("close writer error", e);
            }
        }
    }

    public long getWriteRamUsed() {
        long ram = 0L;
        for (Map.Entry<ShardMetaDefine, WriterAndReadLuceneOperator> entry : memoryMap.entrySet()) {
            ram += entry.getValue().writeRamUsed();
        }
        return ram;
    }




}
