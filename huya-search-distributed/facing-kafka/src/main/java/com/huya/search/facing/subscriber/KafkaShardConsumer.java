package com.huya.search.facing.subscriber;

import com.huya.search.facing.convert.DataConvert;
import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.index.Engine;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.meta.CorrespondTable;
import com.huya.search.index.meta.CorrespondTopic;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.util.WarnService;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/3.
 */
public class KafkaShardConsumer implements ShardConsumer, CorrespondTable, CorrespondTopic {

    public static final Logger LOG = LoggerFactory.getLogger(KafkaShardConsumer.class);

    public static final GroovyClassLoader GROOVY_CLASS_LOADER = new GroovyClassLoader(KafkaShardConsumer.class.getClassLoader());

    private static final long POLL_TIMEOUT = 10000;

    private SubscriberService subscriberService = ModulesBuilder.getInstance().createInjector().getInstance(SubscriberService.class);

    private volatile DataConvert<byte[], byte[], SearchDataRow> dataConvert;

    private String table;

    private int shardId;

    private String topic;

    private Properties properties;

    private TopicPartition topicPartition;

    private KafkaConsumer<byte[], byte[]> consumer;

    private Engine engine;

    private ExecutorService es;

    private PreventDuplication preventDuplication = new DefaultPreventDuplication();

    private volatile boolean isRun = false;

    private CountDownLatch latch;

    public static ShardConsumer newInstance(KafkaSubscriberShard subscriberShard, int shardId) {
        return new KafkaShardConsumer(subscriberShard, shardId);
    }

    private KafkaShardConsumer(KafkaSubscriberShard subscriberShard, int shardId) {
        this.table = subscriberShard.getMetaDefine().getTable();
        this.shardId = shardId;
        this.topic = subscriberShard.getMetaDefine().getTopic();
        this.topicPartition = new TopicPartition(topic, shardId);
        this.properties = subscriberShard.getProperties();
        this.es = subscriberShard.getEs();
        this.engine = subscriberShard.getEngine();
    }

    @SuppressWarnings("unchecked")
    private void initGroovyConvertScript() {
        try {
            String temp = this.table.substring(0, 1).toUpperCase() + this.table.substring(1);
            Class groovyClass = GROOVY_CLASS_LOADER.parseClass(
                    new GroovyCodeSource(Objects.requireNonNull(this.getClass().getClassLoader().getResource("convert/" + temp + "Convert.groovy")))
            );
            dataConvert = (DataConvert<byte[], byte[], SearchDataRow>) groovyClass.newInstance();
            dataConvert.setTable(table);
            dataConvert.setShardId(shardId);
        } catch ( IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRun() {
        return isRun;
    }

    @Override
    public int shardId() {
        return topicPartition.partition();
    }

    private void initStart() {
        isRun = true;
        latch = new CountDownLatch(1);
        this.consumer = new KafkaConsumer<>(properties);
        this.consumer.assign(Collections.singletonList(this.topicPartition));
        initGroovyConvertScript();
    }

    @Override
    public void start() {
        initStart();
        es.submit(() -> pullTask(false));
    }

    @Override
    public void startFromEnd() {
        initStart();
        es.submit(() -> pullTask(true));
    }

    @Override
    public void stop() {
        isRun = false;
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (isRun) {
            stop();
        }
        if (consumer!= null) {
            consumer.close();
            LOG.info("table {} shardId {} topic {} is closed", table, shardId, topic);
        }
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getKafkaBootstrapServers() {
        return this.properties.getProperty("bootstrap.servers");
    }

    private void pullTask(boolean fromEnd) {
        LOG.info(getTopic() + " shardId " + shardId + " subscriber stared");
        long pullNum = 0;
        long maxOffset = 0;
        ConsumerRecords<byte[], byte[]> records;
        try {
            //先定位 Offset 位置，并开始拉取
            if (!fromEnd) {
                try {
                    records = preventDuplication.preventDuplicationSeekAnaPoll();
                } catch (ShardsOperatorException e) {
                    isRun = false;
                    LOG.error("table " + table + " shardId " + shardId + "  preventDuplicationSeekAnaPoll error", e);
                    WarnService.getInstance().send("table " + table + " shardId " + shardId + "  preventDuplicationSeekAnaPoll error", e);
                    subscriberService.preventDuplicationSeekAnaPollException(e);
                    return;
                }
            }
            else {
                this.consumer.seekToEnd(Collections.singleton(topicPartition));
                records = consumer.poll(POLL_TIMEOUT);
            }

            //循环拉取
            while (isRun) {
                SearchDataRow row = null;
                try {
                    for (ConsumerRecord<byte[], byte[]> record : records) {

                        try {
                            row = convert(record);
                        } catch (IgnorableConvertException e) {
                            //todo 根据命令打开或者关闭日志的打印
//                            Object value = record.value();
                            LOG.error(table + "convert  error " + e.getMessage(), e);
                            continue;
                        }

                        try {
                            engine.put(table, row);
                            maxOffset = row.getOffset();
                        } catch (ShardsOperatorException e) {
                            isRun = false;
                            LOG.error("engine put row error : " + row, e);
                            WarnService.getInstance().send("engine put row error : " + row, e);
                            subscriberService.putException(e);
                        }
                    }
                    pullNum += records.count();
                    records = consumer.poll(POLL_TIMEOUT);
                } catch (Exception e) {
                    LOG.error("kafka poll to index error : " + row, e);
                    e.printStackTrace();
                }
            }
            consumer.commitSync();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            LOG.info("table {} shardId {} pull total num is {}  maxOffset is {}", table, shardId, pullNum, maxOffset);
            latch.countDown();
        }
    }

    private SearchDataRow convert(ConsumerRecord<byte[], byte[]> record) throws IgnorableConvertException {
        long offset = record.offset();
        byte[] k = record.key();
        byte[] t = record.value();
        return dataConvert.convert(shardId, offset, t, k);
    }

    private boolean existInEngine(SearchDataRow row) throws ShardsOperatorException {
        long offset = row.getOffset();
        long unixTime = row.getUnixTime();
        return engine.exist(table, shardId, offset, unixTime);
    }

    public class DefaultPreventDuplication implements PreventDuplication {

        private static final long MINRANGE = 5000000;

        @Override
        public ConsumerRecords<byte[], byte[]> preventDuplicationSeekAnaPoll() throws ShardsOperatorException {
            long lastPartitionMaxOffset = engine.lastPartitionMaxOffset(table, shardId);

            return preventDuplicationSeekAnaPoll(lastPartitionMaxOffset);
        }


        private ConsumerRecords<byte[], byte[]> preventDuplicationSeekAnaPoll(long maxOffset) throws ShardsOperatorException {
            //最新的分区里没有数据
            if (maxOffset == -1) {
                maxOffset = quickSeek();
            }

            LOG.info("quickSeek shard {} maxOffset {}", shardId, maxOffset);

            return iterationPreventDuplication(++ maxOffset);
        }

        private long quickSeek() throws ShardsOperatorException {
            long maxOffset = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);
            long minOffset = consumer.beginningOffsets(Collections.singleton(topicPartition)).get(topicPartition);

            consumer.seek(topicPartition, minOffset);
            PullState pullState = getExistOffset();

            if (!pullState.isExist()) {
                SearchDataRow row = pullState.getRow();
                if (row != null) {
                    return row.getOffset();
                }
                else {
                    return minOffset;
                }
            }
            else {
                long unExistOffsetValue = maxOffset;
                long existOffsetValue = minOffset;

                while(unExistOffsetValue - existOffsetValue > MINRANGE) {
                    long current = (unExistOffsetValue + existOffsetValue) / 2;

                    LOG.info("shard {} unExistOffsetValue {} existOffsetValue {} current {}", shardId, unExistOffsetValue, pullState, current);

                    consumer.seek(topicPartition, current);
                    PullState currentPullState = getExistOffset();

                    if (currentPullState.isExist()) {
                        existOffsetValue = currentPullState.row.getOffset();
                    }
                    else {
                        unExistOffsetValue = currentPullState.row.getOffset();
                    }
                }
                return existOffsetValue;
            }
        }


        /**
         * 避免重复的拉取方法
         * @return 应该拉取的最早的记录（既是重新续上之前索引里的 Offset）
         */
        private ConsumerRecords<byte[], byte[]> iterationPreventDuplication(long offset) throws ShardsOperatorException {
            consumer.seek(topicPartition, offset);

            PullState pullState = getExistOffset();

            if (pullState.isEmpty()) {
                return pullState.getRecords();
            }
            else if(pullState.isExist()) {
                long maxOffset = engine.maxOffset(table, shardId, pullState.getRow().getUnixTime()) + 1;
                LOG.info("table {} shardId {} get maxOffset in {}", table, shardId, maxOffset);
                return iterationPreventDuplication(maxOffset);
            }
            else {
                return pullState.getRecords();
            }
        }
    }

    private PullState getExistOffset() throws ShardsOperatorException {
        ConsumerRecords<byte[], byte[]> records;
        PullState pullState;
        while (!(records = consumer.poll(POLL_TIMEOUT)).isEmpty()) {
            Iterator<? extends ConsumerRecord<byte[], byte[]>> iterator = records.iterator();
            ConsumerRecord<byte[], byte[]> record = null;
            SearchDataRow row = null;
            boolean exist = false;

            while (iterator.hasNext()) {
                record = iterator.next();
                try {
                    row = convert(record);
                    exist = existInEngine(row);
                    break;
                } catch (IgnorableConvertException e) {
                    LOG.error(table + "get existInEngine flag error " + record + e.getMessage(), e);
                    record = null;
                }
            }

            if (record == null) continue;

            pullState = PullState.newInstance().setExist(exist).setRow(row).setRecords(records);
            LOG.info("getExistOffset: " + pullState);
            return pullState;

        }

        pullState = PullState.newInstance().setEmpty(true).setExist(false).setRow(null).setRecords(records);
        LOG.info("getExistOffset: " + pullState);
        return pullState;
    }



    static class PullState {
        private boolean empty;
        private boolean exist;
        private ConsumerRecords<byte[], byte[]> records;
        private SearchDataRow row;

        public static PullState newInstance() {
            return new PullState();
        }

        public boolean isEmpty() {
            return empty;
        }

        public PullState setEmpty(boolean empty) {
            this.empty = empty;
            return this;
        }

        public boolean isExist() {
            return exist;
        }

        public PullState setExist(boolean exist) {
            this.exist = exist;
            return this;
        }

        public ConsumerRecords<byte[], byte[]> getRecords() {
            return records;
        }

        public PullState setRecords(ConsumerRecords<byte[], byte[]> records) {
            this.records = records;
            return this;
        }

        public SearchDataRow getRow() {
            return row;
        }

        public PullState setRow(SearchDataRow row) {
            this.row = row;
            return this;
        }

        @Override
        public String toString() {
            return "PullState{" +
                    "empty=" + empty +
                    ", exist=" + exist +
                    ", row=" + row +
                    '}';
        }
    }


}
