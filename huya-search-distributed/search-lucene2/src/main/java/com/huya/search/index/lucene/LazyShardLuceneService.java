package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.ShardCoordinate;
import com.huya.search.memory.MemoryCore;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.settings.Settings;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/31.
 */
@Singleton
public class LazyShardLuceneService extends LuceneService {

    private static final Logger LOG = LoggerFactory.getLogger(LazyShardLuceneService.class);

    private LazyFileSystem lazyFileSystem;

    private ExecutorService es = Executors.newCachedThreadPool();

    private Map<ShardMetaDefine, CountDownLatch> latchMap = new ConcurrentHashMap<>();

    private ReentrantLock createLock = new ReentrantLock();

    @Inject
    LazyShardLuceneService(@Named("Settings") Settings settings, LazyFileSystem lazyFileSystem,
                           @Named("LuceneOperator") MemoryCore<WriterAndReadLuceneOperator> memoryCore) {
        super(settings, memoryCore);
        this.lazyFileSystem = lazyFileSystem;
    }

    @Override
    protected WriterAndReadLuceneOperator getOperator(ShardMetaDefine metaDefine) throws ExecutionException, InterruptedException {
        return getOrCreateOperator(metaDefine);
    }

    private WriterAndReadLuceneOperator getOrCreateOperator(ShardMetaDefine metaDefine) throws InterruptedException, ExecutionException {
        WriterAndReadLuceneOperator luceneOperator = null;
        while (luceneOperator == null) {
            luceneOperator = getOrCreateFutureOperator(metaDefine).get();
        }
        return luceneOperator;
    }

    //Future Object get may be is null , because in period have a thread to remove
    private Future<WriterAndReadLuceneOperator> getOrCreateFutureOperator(ShardMetaDefine metaDefine) {
        createLock.lock();
        try {
            return addGetOrCreateTask(metaDefine);
        } finally {
            createLock.unlock();
        }
    }

    private Future<WriterAndReadLuceneOperator> addGetOrCreateTask(ShardMetaDefine metaDefine) {
        WriterAndReadLuceneOperator luceneOperator = getMemoryObject(metaDefine);
        if (luceneOperator != null) {
            return es.submit(() ->luceneOperator);
        }
        else {
            CountDownLatch latch = latchMap.get(metaDefine);
            if (latch == null) {
                latch = new CountDownLatch(1);
                latchMap.put(metaDefine, latch);
                return es.submit(new CreateLuceneOperator(metaDefine, latch));
            } else {
                return es.submit(new WaitCreateLuceneOperator(metaDefine, latch));
            }
        }
    }

    @Override
    public void removeOperator(ShardMetaDefine metaDefine) throws IOException, InterruptedException {
        if (createLock.tryLock(5, TimeUnit.SECONDS)) {
            try {
                CountDownLatch latch = latchMap.remove(metaDefine);
                if (latch != null) {
                    free(metaDefine);
                }
            } finally {
                createLock.unlock();
            }
        }
    }

    @Override
    public OperatorPool getOperatorPool(TimelineMetaDefine metaDefine) {
        return new OperatorsService(this, metaDefine);
    }

    @Override
    public List<PartitionCycle> getLastPartitionCycle(ShardCoordinate shardCoordinate) throws IOException {
        return lazyFileSystem.getLastDirectoryPartitionCycles(shardCoordinate);
    }

    @Override
    public void lockShards(ShardCoordinate shardCoordinate) throws IOException {
        lazyFileSystem.lockShards(shardCoordinate);
    }

    @Override
    public void unLockShards(ShardCoordinate shardCoordinate) throws IOException {
        lazyFileSystem.unLockShards(shardCoordinate);
    }


    @Override
    protected void notifyApply(ShardMetaDefine metaDefine) {
        //do nothing
    }

    @Override
    protected void apply(WriterAndReadLuceneOperator luceneOperator) throws IOException {
        long start = System.currentTimeMillis();
        luceneOperator.loadWriter();
        luceneOperator.loadReader();
        long end   = System.currentTimeMillis();
        LOG.info("apply luceneOperator " + luceneOperator.tag() + " use time : " + (end - start) + " ms");
    }

    @Override
    protected void notifyFree(ShardMetaDefine metaDefine) {
        //do nothing
    }

    @Override
    protected void free(WriterAndReadLuceneOperator luceneOperator) throws IOException {
        if (luceneOperator != null) {
            luceneOperator.close();
        }
    }

    @Override
    protected void doStart() throws SearchException {
        lazyFileSystem.setPriority(priority() + 1);
        lazyFileSystem.start();
        LOG.info(getName() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        lazyFileSystem.stop();
        LOG.info(getName() + " stopped");
    }

    @Override
    protected void doClose() throws SearchException {
        closeMemoryObject();
        lazyFileSystem.close();
        LOG.info(getName() + " closed");
    }

    @Override
    public String getName() {
        return "LazyShardLuceneService";
    }

    private Directory initDirectory(ShardMetaDefine metaDefine) throws IOException {
        return lazyFileSystem.getDirectory(metaDefine);
    }

    private class CreateLuceneOperator implements Callable<WriterAndReadLuceneOperator> {

        private ShardMetaDefine metaDefine;

        private CountDownLatch latch;

        CreateLuceneOperator(ShardMetaDefine metaDefine, CountDownLatch latch) {
            this.metaDefine = metaDefine;
            this.latch = latch;
        }

        @Override
        public WriterAndReadLuceneOperator call() throws IOException {
            long start = System.currentTimeMillis();
            WriterAndReadLuceneOperator luceneOperator = LuceneOperatorFactory.createReady(
                    metaDefine,
                    initDirectory(metaDefine)
            );
            apply(luceneOperator, metaDefine);
            long end = System.currentTimeMillis();
            LOG.info("create luceneOperator use time : " + (end - start) + " ms");
            latch.countDown();
            return luceneOperator;
        }
    }

    private class WaitCreateLuceneOperator implements Callable<WriterAndReadLuceneOperator> {

        private ShardMetaDefine metaDefine;

        private CountDownLatch latch;

        WaitCreateLuceneOperator(ShardMetaDefine metaDefine, CountDownLatch latch) {
            this.metaDefine = metaDefine;
            this.latch = latch;
        }

        @Override
        public WriterAndReadLuceneOperator call() throws InterruptedException {
            latch.await();
            return getMemoryObject(metaDefine);
        }
    }

    public class OperatorsService implements OperatorPool {

        private LuceneService service;

        private TimelineMetaDefine timelineMetaDefine;

        private OperatorsService(LuceneService service, TimelineMetaDefine timelineMetaDefine) {
            this.service = service;
            this.timelineMetaDefine = timelineMetaDefine;
        }

        @Override
        public WriterAndReadLuceneOperator getOperator(PartitionCycle cycle, int shardId) throws ExecutionException, InterruptedException {
            ShardMetaDefine metaDefine = timelineMetaDefine.getMetaDefineByCycle(cycle, shardId);
            return service.getOperator(metaDefine);
        }

        @Override
        public WriterAndReadLuceneOperator getOperator(long unixTime, int shardId) throws ExecutionException, InterruptedException {
            ShardMetaDefine metaDefine = timelineMetaDefine.getMetaDefineByUnixTime(unixTime, shardId);
            return service.getOperator(metaDefine);
        }

        @Override
        public void removeOperator(ShardMetaDefine metaDefine) throws IOException, InterruptedException {
            service.removeOperator(metaDefine);
        }

        /**
         * 当前周期向前获取 num 个 BaseLuceneOperator
         * @param shardCoordinate 分片定位
         * @param num 个数
         * @return BaseLuceneOperator 列表
         */
        @Override
        public List<WriterAndReadLuceneOperator> getLastOperators(ShardCoordinate shardCoordinate, int num) throws ExecutionException, InterruptedException {
            PartitionCycle cycle = new PartitionCycle(System.currentTimeMillis(), timelineMetaDefine.getLast().getGrain());
            List<WriterAndReadLuceneOperator> luceneOperators = new ArrayList<>(num);
            int shardId = shardCoordinate.getShardId();
            while (num > 0) {
                luceneOperators.add(getOperator(cycle, shardId));
                cycle = cycle.prev();
                num --;
            }
            return luceneOperators;
        }

        @Override
        public void lockShards(ShardCoordinate shardCoordinate) throws IOException {
            LOG.info("lock {} shards", shardCoordinate);
            service.lockShards(shardCoordinate);
        }

        @Override
        public void unLockShards(ShardCoordinate shardCoordinate) throws IOException {
            LOG.info("unlock {} shards", shardCoordinate);
            service.unLockShards(shardCoordinate);
        }
    }
}
