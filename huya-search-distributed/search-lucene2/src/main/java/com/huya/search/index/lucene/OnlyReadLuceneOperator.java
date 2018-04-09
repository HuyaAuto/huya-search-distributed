package com.huya.search.index.lucene;

import com.google.common.collect.Range;
import com.huya.search.index.data.merger.AggrFunUnitSet;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.memory.UseFreq;
import com.huya.search.partition.PartitionCycle;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 只读 Lucene 目录操作类
 * Created by zhangyiqun1@yy.com on 2018/1/29.
 */
public class OnlyReadLuceneOperator extends ReadLuceneOperator {

    protected ReentrantReadWriteLock lock;

    protected IndexReaderContainer reader = ReadyIndexReaderContainer.newInstance();

    public OnlyReadLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                                  IndexWriterConfigFactory configFactory, LuceneOperatorType luceneOperatorType) {
        super(metaDefine, directory, configFactory, luceneOperatorType);
        this.lock = new ReentrantReadWriteLock();
    }

    public OnlyReadLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                                  IndexWriterConfigFactory configFactory, UseFreq useFreq, LuceneOperatorType luceneOperatorType) {
        super(metaDefine, directory, configFactory, useFreq, luceneOperatorType);
        this.lock = new ReentrantReadWriteLock();
    }

    public OnlyReadLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                                  IndexWriterConfigFactory configFactory, LuceneOperatorType luceneOperatorType, ReentrantReadWriteLock lock) {
        super(metaDefine, directory, configFactory, luceneOperatorType);
        this.lock = lock;
    }

    public OnlyReadLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                                  IndexWriterConfigFactory configFactory, UseFreq useFreq,
                                  LuceneOperatorType luceneOperatorType, ReentrantReadWriteLock lock) {
        super(metaDefine, directory, configFactory, useFreq, luceneOperatorType);
        this.lock = lock;
    }

    @Override
    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    @Override
    public void tryClose() throws IOException, InterruptedException {
        if (getLock().writeLock().tryLock(5, TimeUnit.SECONDS)) {
            try {
                closeReader();
            } finally {
                getLock().writeLock().unlock();
            }
        }
    }

    @Override
    public void close() throws IOException {
        getLock().writeLock().lock();
        try {
            closeReader();
            LOG.info("{} close success", tag());
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public void rollbackClose() throws IOException {
        close();
    }

    @Override
    public boolean isClose() {
        return !reader.isOpen();
    }

    @Override
    public long writeRamUsed() {
        return 0;
    }

    @Override
    public void triggerTimeSensitiveEvent(PartitionCycle cycle) throws IOException {
        //do nothing
    }

    @Override
    public boolean exist(long offset) throws IOException {
        return reader.exist(offset);
    }

    @Override
    public long maxOffset() throws IOException {
        return reader.maxOffset();
    }

    @Override
    public List<? extends Iterable<IndexableField>> query(Query query, Set<String> fields, int from, int to) throws IOException {
        addReadFreq();
        return reader.query(query, fields, from, to);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryNew(Query query, Set<String> fields, int from, int to) throws IOException {
        addReadFreq();
        return reader.queryNew(query, fields, from, to);
    }

    @Override
    public List<? extends Iterable<IndexableField>> querySort(Query query, Sort sort, Set<String> fields, int from, int to) throws IOException {
        addReadFreq();
        return reader.querySort(query, sort, fields, from, to);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryAggr(Query query, AggrFunUnitSet aggrFunUnitSet) throws IOException {
        addReadFreq();
        return reader.queryAggr(query, aggrFunUnitSet);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query) throws IOException {
        addReadFreq();
        return reader.queryCount(query);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query, List<Range<Long>> rangeList) throws IOException {
        addReadFreq();
        return reader.queryCount(query, rangeList);
    }

    @Override
    public void refresh(boolean forced) throws IOException {
        lock.readLock().lock();
        try {
            reader.refresh();
            addRefreshFreq();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void closeReader() throws IOException {
        lock.writeLock().lock();
        try {
            if (reader.isOpen()) {
                reader.close();
                LOG.info("{} read close success", tag());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void loadReader() throws IOException {
        lock.readLock().lock();
        long start = System.currentTimeMillis();
        try {
            if (!reader.isOpen()) {
                try {
                    openReader();
                } catch (IOException e) {
                    if (reader.isOpen()) {
                        reader.close();
                    }
                    throw e;
                }
            }
        } finally {
            long end  = System.currentTimeMillis();
            LOG.info("create index reader : " + (end - start) + "ms");
            lock.readLock().unlock();
        }
    }

    protected void openReader() throws IOException {
        reader.open(directory);
    }

    @Override
    public boolean isLoadReader() {
        return reader.isOpen();
    }
}
