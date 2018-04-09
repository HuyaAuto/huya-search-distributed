package com.huya.search.index.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.lucene.OperatorPool;
import com.huya.search.index.lucene.WriterAndReadLuceneOperator;
import com.huya.search.index.opeation.*;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.util.JsonUtil;
import com.huya.search.util.ThreadPoolSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/8.
 */
public class IndexShards implements Shards {

    private static final Logger LOG = LoggerFactory.getLogger(IndexShards.class);

    private static final ExecutorService es = ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC);

    private ShardCoordinate shardCoordinate;

    private OperatorPool pool;

    private volatile State state = State.CLOSE;

    private Map<Long, Shard> shardMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    public static IndexShards newInstance(OperatorPool pool, ShardCoordinate shardCoordinate) {
        return new IndexShards(pool, shardCoordinate);
    }

    protected IndexShards(OperatorPool pool, ShardCoordinate shardCoordinate) {
        this.pool = pool;
        this.shardCoordinate = shardCoordinate;
    }

    @Override
    public boolean isOpen() {
        return state == State.OPEN;
    }

    @Override
    public long lastPartitionMaxOffset() throws ShardsOperatorException {
        try {
            //todo 修改通过参数配置启动时打开最近的几个分区目录
            List<WriterAndReadLuceneOperator> lastOperators = pool.getLastOperators(shardCoordinate, 4);
            List<Shard> temp = new ArrayList<>();
            for (WriterAndReadLuceneOperator luceneOperator : lastOperators) {
                Shard shard = ShardFactory.create(luceneOperator, shardCoordinate.getShardId());
                temp.add(shard);
                long unixTime = luceneOperator.getPartitionCycle().getFloor();
                shardMap.put(unixTime, shard);
            }
            return queryMaxOffset(temp);
        } catch (ExecutionException | InterruptedException e) {
            if (e instanceof ExecutionException) {
                throw ShardsOperatorException.createShardFail(shardCoordinate);
            }
            else {
                throw ShardsOperatorException.interrupted(shardCoordinate);
            }
        }

    }

    private long queryMaxOffset(List<Shard> list) throws ShardsOperatorException {
        long maxOffset = -1;
        Future[] offsetFutureArray = new Future[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Shard shard = list.get(i);
            offsetFutureArray[i] = es.submit(shard::maxOffset);
        }

        try {
            for (int i = 0; i < list.size(); i++) {
                maxOffset = Math.max(maxOffset, ((long) offsetFutureArray[i].get()));
            }
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof ExecutionException) {
                if (e.getCause() instanceof  ShardsOperatorException) {
                    throw (ShardsOperatorException) e.getCause();
                }
                else {
                    e.printStackTrace();
                }
            }
            else {
                throw ShardsOperatorException.interrupted(shardCoordinate);
            }
        }
        return maxOffset;
    }

    @Override
    public Shard getShard(long unixTime) throws ShardsOperatorException {
        Shard shard = shardMap.get(unixTime);
        if (shard == null) {
            shard = createShard(unixTime);
            shardMap.put(unixTime, shard);
        }
        return shard;
    }

    private Shard createShard(long floorUnixTime) throws ShardsOperatorException {
        try {
            int retry = 3;
            WriterAndReadLuceneOperator luceneOperator = null;
            while (luceneOperator == null) {
                luceneOperator = pool.getOperator(floorUnixTime, shardCoordinate.getShardId());
                if (--retry <= 0) {
                    break;
                }
            }

            if (luceneOperator != null) {
                return ShardFactory.create(luceneOperator, shardCoordinate.getShardId());
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            LOG.error("create shard fail", e);
        }
        throw ShardsOperatorException.createShardFail(shardCoordinate);
    }

    @Override
    public QueryShards getQueryShards(Collection<Long> unixTimes) {
        List<LazyLuceneOperator> futures = new ArrayList<>(unixTimes.size());
        unixTimes.forEach(unixTime -> futures.add(
                FutureLazyLuceneOperator.newInstance(es.submit(() -> getShard(unixTime))))
        );
        return QueryShards.DefaultQueryShards.newInstance(futures);
    }

    @Override
    public void check() {
        shardMap.forEach((key, value) -> {
            if (value.check()) {
                try {
                    Shard shard = shardMap.remove(key);
                    pool.removeOperator(shard.getMetaDefine());
                    LOG.info("close {} success", value.getMetaDefine());
                } catch (IOException | InterruptedException e) {
                    LOG.error("close " + value.getMetaDefine() + " error", e);
                }
            }
        });
    }

    @Override
    public void remove(Shard shard) {
        try {
            Shard removeShard = shardMap.remove(shard.getMetaDefine().getPartitionCycle().getFloor());
            if (removeShard != null) {
                pool.removeOperator(shard.getMetaDefine());
                LOG.info("close {} success", shard.getMetaDefine());
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("close " + shard.getMetaDefine() + " error", e);
        }
    }

    @Override
    public void refresh(RefreshContext refreshContext) {
        shardMap.values().forEach(shard -> shard.refresh(refreshContext));
    }

    @Override
    public void triggerTimeSensitiveEvent(PartitionCycle cycle) {
        shardMap.values().forEach(shard -> {
            try {
                shard.triggerTimeSensitiveEvent(cycle);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public JsonNode insertStat() {
        ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
        for (Shard shard : this) {
            arrayNode.add(shard.insertStat());
        }
        return arrayNode;
    }

    @Override
    public void open() throws ShardsOperatorException {
        try {
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                try {
                    lock();
                    state = State.OPEN;
                } finally {
                    lock.unlock();
                }
            } else {
                throw ShardsOperatorException.couldNotGetLock(shardCoordinate);
            }
        } catch (InterruptedException e) {
            throw ShardsOperatorException.couldNotGetLock(shardCoordinate);
        }
    }

    @Override
    public void close() throws ShardsOperatorException {
        try {
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                try {
                    closeShards();
                    unLock();
                    state = State.CLOSE;
                } finally {
                    lock.unlock();
                }
            } else {
                throw ShardsOperatorException.couldNotGetLock(shardCoordinate);
            }
        } catch (InterruptedException e) {
            throw ShardsOperatorException.couldNotGetLock(shardCoordinate);
        }
    }

    /**
     * 创建文件锁
     */
    protected void lock() throws ShardsOperatorException {
        try {
            pool.lockShards(shardCoordinate);
        } catch (IOException e) {
            e.printStackTrace();
            throw ShardsOperatorException.lockIOException(shardCoordinate);
        }
    }

    /**
     * 清除文件锁
     */
    protected void unLock() throws ShardsOperatorException {
        try {
            pool.unLockShards(shardCoordinate);
        } catch (IOException e) {
            throw ShardsOperatorException.lockIOException(shardCoordinate);
        }
    }

    private void closeShards() throws ShardsOperatorException {

        List<Long> cycles = new ArrayList<>();

        for (Map.Entry<Long, Shard> entry : shardMap.entrySet()) {
            long unixTime = entry.getKey();
            Shard shard = entry.getValue();
            try {
                pool.removeOperator(shard.getMetaDefine());
                shardMap.remove(unixTime);
                LOG.info("close {} success", shard.getMetaDefine());
            } catch (IOException | InterruptedException e) {
                LOG.info("close " + shard.getMetaDefine() + " error", e);
                cycles.add(unixTime);
            }
        }

        if (cycles.size() > 0) {
            CyclesCoordinate cyclesCoordinate = new CyclesCoordinateContext(shardCoordinate, cycles);
            throw ShardsOperatorException.closeIOException(cyclesCoordinate);
        }

    }

    @Override
    public Iterator<Shard> iterator() {
        return shardMap.values().iterator();
    }

    enum State {
        OPEN,
        CLOSE
    }
}
