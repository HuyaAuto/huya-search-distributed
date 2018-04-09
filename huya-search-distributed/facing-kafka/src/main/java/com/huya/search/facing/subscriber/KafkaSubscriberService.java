package com.huya.search.facing.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.index.Engine;
import com.huya.search.SearchException;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.block.TableShardServiceAble;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.CloseContext;
import com.huya.search.settings.Settings;
import com.huya.search.util.JsonUtil;
import com.huya.search.util.WarnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

@Singleton
public class KafkaSubscriberService extends SubscriberService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaSubscriberService.class);

    private Settings settings;

    private Engine engine;

    private MetaService metaService;

    private ExecutorService es = Executors.newCachedThreadPool();

    private Map<String, Subscriber> subMap = new ConcurrentHashMap<>();

    private int pullRetry;

    private int pullWaitTime;

    private TableShardServiceAble tableShardServiceAble;

    @Inject
    public KafkaSubscriberService(@Named("Kafka-Subscriber") Settings settings, Engine engine, MetaService metaService, TableShardServiceAble tableShardServiceAble) {
        this.settings = settings;
        this.engine   = engine;
        this.metaService = metaService;
        this.pullRetry = settings.getAsInt("search.pull.retry", 3);
        this.pullWaitTime = settings.getAsInt("search.pull.waitTime", 10);
        this.tableShardServiceAble = tableShardServiceAble;
    }

    @Override
    protected Properties getSubscriberSettings() {
        return settings.asProperties("search", false);
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public void openPullTask(String table, int shardId) throws ShardsOperatorException {
        openPullTask(table, shardId, false);
    }

    @Override
    public void openPullTaskFromEnd(String table, int shardId) throws ShardsOperatorException {
        openPullTask(table, shardId, true);
    }

    private void openPullTask(String table, int shardId, boolean fromEnd) throws ShardsOperatorException {
        LOG.info("open pull task {} {}", table, shardId);
        Subscriber subscriber = null;
        int retry = 0;
        while (subscriber == null) {
            subscriber = subMap.get(table);
            if (subscriber != null) {
                if (! subscriber.isRun(shardId)) { //如果订阅还没用启动则开始启动
                    engine.openShards(table, shardId);
                    if (!fromEnd) {
                        subscriber.startKafkaConsumer(shardId);
                    }
                    else {
                        subscriber.startKafkaConsumerFromEnd(shardId);
                    }
                }
            } else {
                LOG.warn("table is not open, wait {} seconds and retry", pullWaitTime);
                if (++retry > pullRetry) {
                    LOG.error("retry {} , but table is not open", pullRetry);
                    WarnService.getInstance().send("table " + table + " shard " + shardId + " is not open", new RuntimeException("retry " + pullRetry));
                    break;
                }

                try {
                    TimeUnit.SECONDS.sleep(pullWaitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void closePullTask(String table, int shardId) throws ShardsOperatorException {
        LOG.info("close pull task {} {}", table, shardId);
        Subscriber subscriber = subMap.get(table);
        if (subscriber != null) {
            subscriber.closeKafkaConsumer(shardId);
        }
        engine.closeShards(table, shardId);
    }

    @Override
    public void closePullTask(String table, List<Integer> shardIds) throws ShardsOperatorException {
        LOG.info("close pull task {} {}", table, shardIds);
        Subscriber subscriber = subMap.get(table);
        if (subscriber != null) {
            shardIds.forEach(subscriber::closeKafkaConsumer);
        }

        final List<ShardsOperatorException> exceptionList = new Vector<>();
        shardIds.parallelStream().forEach(shardId -> {
            try {
                engine.closeShards(table, shardId);
            } catch (ShardsOperatorException e) {
                exceptionList.add(e);
            }

        });

        if (exceptionList.size() > 0) {
            throw ShardsOperatorException.closeIOException(exceptionList);
        }
    }

    @Override
    public JsonNode getInsertStat() {
        return engine.insertStat();
    }

    public void shardsCloseException(ShardsOperatorException e) {
        //todo 进行相应操作进行恢复
    }

    /**
     * 注意此函数需要异步处理
     * @param e
     */
    @Override
    public void preventDuplicationSeekAnaPollException(ShardsOperatorException e) {
        //todo 进行相应操作进行恢复
    }

    /**
     * 注意此函数需要异步处理
     * @param e
     */
    @Override
    public void putException(ShardsOperatorException e) {
        if (e instanceof ShardsOperatorException.ShardsLuceneWriterIsCloseException) {
            ShardsOperatorException.ShardsLuceneWriterIsCloseException isCloseException =
                    (ShardsOperatorException.ShardsLuceneWriterIsCloseException) e;
            //todo 尝试再次打开 IndexShard
            new RuntimeException("table " + isCloseException.getTable() + " shardId " + isCloseException.getShardId() + " cycle " + isCloseException.getCycle());
        }
        else {
            //todo 进行相应操作进行恢复
        }
    }

    @Override
    public JsonNode syncPullTask() {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();

        subMap.forEach((key, value) -> objectNode.set(key, value.syncPullTask()));
        return objectNode;
    }

    @Override
    public void open(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        if (!subMap.containsKey(table)) {
            Subscriber subscriber = new KafkaSubscriber(getSubscriberSettings(), engine, metaDefine, es, tableShardServiceAble.getShardInfos(table));
            subMap.put(table, subscriber);
        }
    }

    @Override
    public void close(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        Subscriber subscriber = subMap.remove(table);
        if (subscriber != null && subscriber.isRun()) {
            subscriber.close();
        }
    }

    @Override
    protected void doStart() throws SearchException {
        metaService.register(this);
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        //按照正常的关闭顺序，所有的 subscriber 已经关闭
        assert subMap.size() == 0;
        es.shutdown();
    }

    @Override
    public String getName() {
        return "SubscriberService";
    }
}
