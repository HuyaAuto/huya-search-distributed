package com.huya.search.index.lucene;

import com.google.common.collect.Range;
import com.huya.search.index.data.merger.AggrFunUnitSet;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.InsertContext;
import com.huya.search.memory.UseFreq;
import com.huya.search.partition.PartitionCycle;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/1.
 */
public class RamLuceneOperator extends ReadyLuceneOperator {

    public static final long BATCH = 1000000;

    public static final long FLUSH_RAM = 300000000L;

    private AtomicInteger batchNum = new AtomicInteger(1);

    private RamTwoPhaseLuceneOperator parentLuceneOperator;

    private UseFreq ramUseFreq;

    private volatile ReadyLuceneOperator currentOperator;

    private volatile boolean openRam = true;

    private ReentrantLock ramSwitchLock = new ReentrantLock();

    RamLuceneOperator(ShardMetaDefine metaDefine, Directory directory, IndexWriterConfigFactory configFactory,
                      RamTwoPhaseLuceneOperator ramTwoPhaseLuceneOperator, UseFreq ramUseFreq) {
        super(metaDefine, directory, configFactory, LuceneOperatorType.RAM);
        this.parentLuceneOperator = ramTwoPhaseLuceneOperator;
        this.ramUseFreq = ramUseFreq;

        this.currentOperator = new ReadyLuceneOperator(metaDefine, new RAMDirectory(), configFactory, ramUseFreq, LuceneOperatorType.RAM_SHARD);
    }

    @Override
    public void write(InsertContext context) throws IOException {
        currentOperator.write(context);
        if (currentOperator.writeTotal() >= batchNum.get() * BATCH || writeRamUsed() + directoryRamUsed() >= FLUSH_RAM) {
            ramSwitchLock.lock();
            batchNum.addAndGet(1);
            try {
                if (currentOperator != null && !currentOperator.isClose()) {

                    //加载下一个内存目录
                    ReadyLuceneOperator nextOperator = new ReadyLuceneOperator(metaDefine, new RAMDirectory(), configFactory, ramUseFreq, LuceneOperatorType.RAM);
                    nextOperator.loadWriter();
                    nextOperator.loadReader();

                    //交换引用
                    ReadyLuceneOperator temp = currentOperator;
                    currentOperator = nextOperator;

                    //合并内存数据成为一个段，再将内存目录刷新到磁盘
                    LOG.info("{} flush ram to disk", temp.tag());
                    flushRamToDisk(temp);
                    Runtime.getRuntime().gc();

                }
            } finally {
                ramSwitchLock.unlock();
            }
        }

    }

    @Override
    public void loadWriter() throws IOException {
        currentOperator.loadWriter();
    }

    @Override
    public boolean isLoadWriter() {
        return currentOperator.isLoadWriter();
    }

    @Override
    public void addIndexes(Directory... dirs) throws IOException {
        throw new IOException("ram lucene operator no support add indexes");
    }

    @Override
    public long writeRamUsed() {
        return currentOperator == null || currentOperator.isClose() ? 0 : currentOperator.writeRamUsed();
    }

    public long directoryRamUsed() {
        return currentOperator == null || currentOperator.isClose() ? 0 : ((RAMDirectory) currentOperator.directory).ramBytesUsed();
    }

    public Directory getRamDirectory() {
        return currentOperator == null || currentOperator.isClose() ? null : currentOperator.directory;
    }

    @Override
    public void flush() throws IOException {
        if (currentOperator != null && !currentOperator.isClose()) {
            currentOperator.flush();
        }
    }

    @Override
    public void forceMerge(int num) throws IOException {
        if (currentOperator != null && !currentOperator.isClose()) {
            currentOperator.forceMerge(num);
        }
    }

    @Override
    public void closeWriter() throws IOException {
        if (currentOperator != null && !currentOperator.isClose()) {
            currentOperator.closeWriter();
        }
    }

    @Override
    public void rollbackCloseWriter() throws IOException {
        if (currentOperator != null && !currentOperator.isClose()) {
            currentOperator.rollbackCloseWriter();
        }
    }

    public void closeRam() throws IOException {
        if (this.openRam) {
            ramSwitchLock.lock();
            try {
                this.openRam = false;
                flushRamToDisk(currentOperator);
                currentOperator = null; //gc 标志
                Runtime.getRuntime().gc();
            } finally {
                ramSwitchLock.unlock();
            }
        }
    }

    private void flushRamToDisk(ReadyLuceneOperator luceneOperator) throws IOException {
        luceneOperator.forceMerge(1);
        luceneOperator.close();
        parentLuceneOperator.addIndexes(luceneOperator.directory);
        parentLuceneOperator.refresh(true);
    }

    public boolean isOpenRam() {
        return openRam;
    }

    @Override
    public ReentrantReadWriteLock getLock() {
        return currentOperator.getLock();
    }

    @Override
    public boolean exist(long offset) throws IOException {
        return currentOperator.exist(offset);
    }

    @Override
    public long maxOffset() throws IOException {
        return currentOperator.maxOffset();
    }

    @Override
    public List<? extends Iterable<IndexableField>> query(Query query, Set<String> fields, int from, int to) throws IOException {
        currentOperator.refresh(false);
        return currentOperator.query(query, fields, from, to);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryNew(Query query, Set<String> fields, int from, int to) throws IOException {
        currentOperator.refresh(false);
        return currentOperator.queryNew(query, fields, from, to);
    }

    @Override
    public List<? extends Iterable<IndexableField>> querySort(Query query, Sort sort, Set<String> fields, int from, int to) throws IOException {
        currentOperator.refresh(false);
        return currentOperator.querySort(query, sort, fields, from, to);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryAggr(Query query, AggrFunUnitSet aggrFunUnitSet) throws IOException {
        currentOperator.refresh(false);
        return currentOperator.queryAggr(query, aggrFunUnitSet);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query) throws IOException {
        currentOperator.refresh(false);
        return currentOperator.queryCount(query);
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query, List<Range<Long>> rangeList) throws IOException {
        currentOperator.refresh(false);
        return currentOperator.queryCount(query, rangeList);
    }

    @Override
    public void refresh(boolean forced) throws IOException {
        throw new RuntimeException("no need refresh ram");
    }

    @Override
    public void closeReader() throws IOException {
        currentOperator.closeReader();
    }

    @Override
    public void loadReader() throws IOException {
        currentOperator.loadReader();
    }

    @Override
    public boolean isLoadReader() {
        return currentOperator.isLoadReader();
    }

    @Override
    public void triggerTimeSensitiveEvent(PartitionCycle cycle) throws IOException {
        closeRam();
    }
}
