package com.huya.search.index.lucene;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Range;
import com.huya.search.index.data.impl.PartitionDocs;
import com.huya.search.index.data.merger.AggrFunUnitSet;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.data.merger.MergerFactory;
import com.huya.search.index.data.merger.TimeCountMerger;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.InsertContext;
import com.huya.search.memory.CombineUseFreq;
import com.huya.search.memory.UseFreq;
import com.huya.search.memory.UseFreqObject;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.util.JsonUtil;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.huya.search.index.data.SingleQueryResult.createNumberIndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/1.
 */
public class RamTwoPhaseLuceneOperator extends ReadyLuceneOperator {

    private RamLuceneOperator ramOperator;

    private ReadyLuceneOperator diskOperator;

    private UseFreq ramPublicUseFreq = UseFreqObject.newInstance();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    RamTwoPhaseLuceneOperator(ShardMetaDefine metaDefine, Directory directory,
                              IndexWriterConfigFactory diskConfigFactory, IndexWriterConfigFactory ramConfigFactory) {
        super(metaDefine, directory, diskConfigFactory, LuceneOperatorType.HDFS_ADN_RAM);
        this.diskOperator = new ReadyLuceneOperator(metaDefine, directory, configFactory, LuceneOperatorType.HDFS);
        this.ramOperator = new RamLuceneOperator(metaDefine, directory, ramConfigFactory, this, ramPublicUseFreq);

        this.setUseFreq(CombineUseFreq.newInstance(diskOperator.getUseFreq(), ramPublicUseFreq));
    }

    @Override
    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    /**
     * 返回 LuceneOperator 是否对某个 PartitionCycle 时间敏感
     * 对于两段式插入 LuceneOperator 是时间敏感的
     * 值对当前时间分区进行两段式插入，当时间周期过去
     * 将功能上将退化为普通 LuceneOperator
     */
    @Override
    public void triggerTimeSensitiveEvent(PartitionCycle cycle) throws IOException {
        lock.writeLock().lock();
        try {
            if (ramOperator.isOpenRam() && metaDefine.getPartitionCycle().getCeil() == cycle.getFloor()) {
                ramOperator.triggerTimeSensitiveEvent(cycle);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void refresh(boolean forced) throws IOException {
        diskOperator.refresh(forced);
    }

    @Override
    public boolean exist(long offset) throws IOException {
        return diskOperator.exist(offset);
    }

    @Override
    public long maxOffset() throws IOException {
        return diskOperator.maxOffset();
    }

    /**
     * 写索引，如果处在两段式写入阶段，新数据直接写入内存
     * 否则写入磁盘
     * @param context 索引内容
     * @throws IOException io 异常
     */
    @Override
    public void write(InsertContext context) throws IOException {
        if (ramOperator.isOpenRam()) {
            lock.readLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    ramOperator.write(context);
                    return;
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        diskOperator.write(context);
    }

    /**
     * 加入其他索引目录
     * @param dirs 目录
     * @throws IOException io 异常
     */
    @Override
    public void addIndexes(Directory... dirs) throws IOException {
        this.diskOperator.addIndexes(dirs);
    }

    /**
     * 写者 flush
     * @throws IOException io 异常
     */
    @Override
    public void flush() throws IOException {
        if (ramOperator.isOpenRam()) {
            lock.readLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    ramOperator.flush();
                    return;
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        diskOperator.flush();
    }

    @Override
    public void forceMerge(int num) throws IOException {
        diskOperator.forceMerge(num);
    }

    /**
     * 关闭写者，处在两段式写入过程，先关闭内存写者，再关闭磁盘写者，否则直接关闭磁盘写者
     * @throws IOException io 异常
     */
    @Override
    public void closeWriter() throws IOException {
        if (ramOperator.isOpenRam()) {
            lock.writeLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    ramOperator.closeWriter();
                    ramOperator.closeRam();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        diskOperator.closeWriter();
    }

    @Override
    public void rollbackCloseWriter() throws IOException {
        if (ramOperator.isOpenRam()) {
            lock.writeLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    ramOperator.rollbackCloseWriter();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        diskOperator.rollbackCloseWriter();
    }

    /**
     * 关闭读者，处在两段式写入过程，先关闭内存读者，再关闭磁盘读者，否则直接关闭磁盘读者
     * @throws IOException io 异常
     */
    @Override
    public void closeReader() throws IOException {
        if (ramOperator.isOpenRam()) {
            lock.writeLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    ramOperator.closeReader();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        diskOperator.closeReader();
    }

    /**
     * 加载读者
     * @throws IOException io 异常
     */
    @Override
    public void loadReader() throws IOException {
        if (ramOperator.isOpenRam()) {
            ramOperator.loadReader();
        }
        diskOperator.loadReader();
    }

    /**
     * 判断是否已经加载读者
     * @return 是否加载读者
     */
    @Override
    public boolean isLoadReader() {
        return diskOperator.isLoadReader();
    }

    /**
     * 内存使用情况
     * @return bytes 数
     */
    @Override
    public long writeRamUsed() {
        long ramUsed = 0L;
        if (ramOperator.isOpenRam()) ramUsed += ramOperator.writeRamUsed();
        ramUsed += diskOperator.writeRamUsed();
        return ramUsed;
    }

    public long directoryRamUsed() {
        return ramOperator.isOpenRam() ? ramOperator.directoryRamUsed() : 0;
    }

    /**
     * 加载写者
     * @throws IOException io 异常
     */
    @Override
    public void loadWriter() throws IOException {
        if (ramOperator.isOpenRam()) {
            ramOperator.loadWriter();
        }
        diskOperator.loadWriter();
    }

    /**
     * 判断是否已加载写者
     * @return 是否加载写者
     */
    @Override
    public boolean isLoadWriter() {
        return diskOperator.isLoadWriter();
    }

    @Override
    public List<? extends Iterable<IndexableField>> query(Query query, Set<String> fields, int from, int to) throws IOException {
        List<Iterable<IndexableField>> temp = new ArrayList<>();
        if (ramOperator.isOpenRam()) {
            lock.readLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    temp.addAll(ramOperator.query(query, fields, from, to));
                    if (temp.size() >= to - from + 1) {
                        return temp;
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        if (temp.size() > 0) {
            to = to - from + 1 - temp.size();
        }

        temp.addAll(diskOperator.query(query, fields, from, to));
        return temp;
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryNew(Query query, Set<String> fields, int from, int to) throws IOException {
        List<Iterable<IndexableField>> temp = new ArrayList<>();
        if (ramOperator.isOpenRam()) {
            lock.readLock().lock();
            try {
                if (ramOperator.isOpenRam()) {
                    temp.addAll(ramOperator.queryNew(query, fields, from, to));
                    if (temp.size() >= to - from + 1) {
                        return temp;
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        if (temp.size() > 0) {
            to = to - from + 1 - temp.size();
        }

        temp.addAll(diskOperator.queryNew(query, fields, from, to));
        return temp;
    }

    @Override
    public List<? extends Iterable<IndexableField>> querySort(Query query, Sort sort, Set<String> fields, int from, int to) throws IOException {
        ExecutorService es = ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);

        Future<List<? extends Iterable<IndexableField>>> ramFutureResult = es.submit(() -> {
            if (ramOperator.isOpenRam()) {
                lock.readLock().lock();
                try {
                    if (ramOperator.isOpenRam()) {
                        return ramOperator.querySort(query, sort, fields, from, to);
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }

            return Collections.emptyList();
        });

        Future<List<? extends Iterable<IndexableField>>> diskFutureResult = es.submit(() ->
                diskOperator.querySort(query, sort, fields, from, to)
        );

        try {
            Merger merger = MergerFactory.createSortMerger(sort, from, to);
            merger.add(PartitionDocs.newInstance("ram", ramFutureResult.get()));
            merger.add(PartitionDocs.newInstance("disk", diskFutureResult.get()));
            return merger.result().getCollection();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("merger ran and disk result error", e);
        }
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryAggr(Query query, AggrFunUnitSet aggrFunUnitSet) {
        //todo 实现 ram 与 disk 的聚合
//        return super.queryAggr(query, aggrFuns, groups);
        return null;
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query) throws IOException {
        ExecutorService es = ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);

        Future<List<? extends Iterable<IndexableField>>> ramFutureResult = es.submit(() -> {
            if (ramOperator.isOpenRam()) {
                lock.readLock().lock();
                try {
                    if (ramOperator.isOpenRam()) {
                        return ramOperator.queryCount(query);
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }

            return Collections.singletonList(Collections.singletonList(createNumberIndexableField("*", 0)));
        });

        Future<List<? extends Iterable<IndexableField>>> diskFutureResult = es.submit(() ->
                diskOperator.queryCount(query)
        );


        try {
            List<Iterable<IndexableField>> temp = new ArrayList<>(ramFutureResult.get());
            temp.addAll(diskFutureResult.get());
            return temp;
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("get result error", e);
        }
    }

    @Override
    public List<? extends Iterable<IndexableField>> queryCount(Query query, List<Range<Long>> rangeList) throws IOException {
        ExecutorService es = ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);

        Future<List<? extends Iterable<IndexableField>>> ramFutureResult = es.submit(() -> {
            if (ramOperator.isOpenRam()) {
                lock.readLock().lock();
                try {
                    if (ramOperator.isOpenRam()) {
                        return ramOperator.queryCount(query, rangeList);
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }

            return Collections.emptyList();
        });

        Future<List<? extends Iterable<IndexableField>>> diskFutureResult = es.submit(() ->
                diskOperator.queryCount(query, rangeList)
        );

        Merger merger = new TimeCountMerger();
        try {
            merger.add(new PartitionDocs("ram", ramFutureResult.get()));
            merger.add(new PartitionDocs("disk", diskFutureResult.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return merger.result().getCollection();
    }

    public UseFreq getRamPublicUseFreq() {
        return ramPublicUseFreq;
    }

    @Override
    public JsonNode report() {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();

        ObjectNode ramObjectNode = (ObjectNode) ramPublicUseFreq.report();
        ramObjectNode.put("ramUsed", ramOperator.directoryRamUsed());
        ramObjectNode.put("luceneOperator", ramOperator.tag());
        ramObjectNode.put("isOPenRam", ramOperator.isOpenRam());
        objectNode.set("ram", ramObjectNode);


        ObjectNode diskObjectNode = (ObjectNode) diskOperator.report();
        diskObjectNode.put("ramUsed", diskOperator.writeRamUsed());
        diskObjectNode.put("luceneOperator", diskOperator.tag());

        objectNode.set("disk", diskObjectNode);
        return objectNode;
    }
}
