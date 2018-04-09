package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.InsertContext;
import com.huya.search.memory.UseFreq;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/11.
 */
class ReadyLuceneOperator extends WriterAndReadLuceneOperator {

    private IndexWriter writer;

    ReadyLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                        IndexWriterConfigFactory configFactory, LuceneOperatorType luceneOperatorType) {
        super(metaDefine, directory, configFactory, luceneOperatorType);
        this.lock = new ReentrantReadWriteLock();
    }

    ReadyLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                        IndexWriterConfigFactory configFactory, UseFreq useFreq, LuceneOperatorType luceneOperatorType) {
        super(metaDefine, directory, configFactory, useFreq, luceneOperatorType);
        this.lock = new ReentrantReadWriteLock();
    }

    ReadyLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                        IndexWriterConfigFactory configFactory, LuceneOperatorType luceneOperatorType, ReentrantReadWriteLock lock) {
        super(metaDefine, directory, configFactory, luceneOperatorType);
        this.lock = lock;
    }

    ReadyLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                        IndexWriterConfigFactory configFactory, UseFreq useFreq,
                        LuceneOperatorType luceneOperatorType, ReentrantReadWriteLock lock) {
        super(metaDefine, directory, configFactory, useFreq, luceneOperatorType);
        this.lock = lock;
    }

    @Override
    public void refresh(boolean forced) throws IOException {
        lock.readLock().lock();
        try {
            if (writer.isOpen() && reader.isOpen()) {
                if (forced && writer.hasUncommittedChanges()) {
                    LOG.info("{} refresh to commit", tag());
                    writer.commit();
                }
                reader.refresh();
                addRefreshFreq();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(InsertContext context) throws IOException {
        WriterContext writerContext = WriterContext.newInstance(context, metaDefine);
        lock.readLock().lock();
        try {
            if (isLoadWriter()) {
                write(writerContext);
            }
            else {
                throw new WriterIsCloseException();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private void write(WriterContext context) throws IOException {
        assert context.hasData();
        writer.addDocument(context.getDocument());
        addWriteFreq();
    }

    @Override
    public void addIndexes(Directory... dirs) throws IOException {
        lock.readLock().lock();
        try {
            writer.addIndexes(dirs);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void flush() throws IOException {
        lock.readLock().lock();
        try {
            if (writer.isOpen()) {
                writer.flush();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void forceMerge(int num) throws IOException {
        lock.readLock().lock();
        try {
            if (writer.isOpen()) {
                writer.forceMerge(num);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void closeWriter() throws IOException {
        try {
            lock.writeLock().lock();
            if (isLoadWriter()) {
                writer.close();
                LOG.info("{} writer close success", tag());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void rollbackCloseWriter() throws IOException {
        try {
            lock.writeLock().lock();
            if (isLoadWriter()) {
                writer.rollback();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long writeRamUsed() {
        return writer.ramBytesUsed();
    }

    @Override
    public void loadWriter() throws IOException {
        loadWriter(directory);
    }

    private void loadWriter(Directory directory) throws IOException {
        lock.readLock().lock();
        long start = System.currentTimeMillis();
        try {
            if (writer == null) {
                writer = new IndexWriter(directory, configFactory.createConfig());
            }
        } finally {
            long end  = System.currentTimeMillis();
            LOG.info("create index writer : " + (end - start) + "ms");
            lock.readLock().unlock();
        }
    }

    @Override
    protected void openReader() throws IOException {
        reader.open(writer);
    }

    @Override
    public boolean isLoadWriter() {
        return writer != null && writer.isOpen();
    }

    @Override
    public void tryClose() throws IOException, InterruptedException {
        if (getLock().writeLock().tryLock(5, TimeUnit.SECONDS)) {
            try {
                closeReader();
                closeWriter();
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
            closeWriter();
            LOG.info("{} close success", tag());
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public void rollbackClose() throws IOException {
        getLock().writeLock().lock();
        try {
            closeReader();
            rollbackCloseWriter();
            LOG.info("{} rollbackClose success", tag());
        } finally {
            getLock().writeLock().unlock();
        }
    }

    @Override
    public boolean isClose() {
        return !isLoadWriter() && super.isClose();
    }

}
