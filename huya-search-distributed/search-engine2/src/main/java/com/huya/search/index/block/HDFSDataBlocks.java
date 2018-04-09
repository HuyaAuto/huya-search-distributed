package com.huya.search.index.block;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.ExecutorContext;
import com.huya.search.index.opeation.RefreshContext;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.partition.TimeSensitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
@Singleton
public class HDFSDataBlocks extends DataBlocks {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    private Future<?> checkFuture;

    private Future<?> refreshFuture;

    private Future<?> triggerTimeFuture;

    private PartitionsCheckTask checkTask = new PartitionsCheckTask();

    private PartitionsRefreshTask refreshTask = new PartitionsRefreshTask();

    private TriggerTimeTask triggerTimeTask = new TriggerTimeTask();

    private Logger LOG = LoggerFactory.getLogger(HDFSDataBlocks.class);

    private Map<String, Partitions> partitionsMap = new ConcurrentHashMap<>();

    private MetaService metaService;

    private PartitionsFactory partitionsFactory;

    private TableShardServiceAble tableShardServiceAble;

    private ShardsFactory shardsFactory;

    @Inject
    protected HDFSDataBlocks(MetaService metaService, PartitionsFactory partitionsFactory,
                             TableShardServiceAble tableShardServiceAble, ShardsFactory shardsFactory) {
        this.metaService = metaService;
        this.partitionsFactory = partitionsFactory;
        this.tableShardServiceAble = tableShardServiceAble;
        this.shardsFactory = shardsFactory;
    }

    @Override
    public Partitions getPartitions(ExecutorContext executorContext) {
        String table = executorContext.getTable();
        Partitions partitions = partitionsMap.get(table);
        if (partitions == null)
            throw new RuntimeException("not found " + table + ", maybe you should open or create it, use metaService");
        return partitions;
    }

    @Override
    protected void doStart() throws SearchException {
        tableShardServiceAble.start();
        metaService.register(this);
        //todo 重构代码添加 check 和 refresh
        checkFuture       = scheduledExecutorService.scheduleAtFixedRate(checkTask, 3000, 3000, TimeUnit.SECONDS);
        refreshFuture     = scheduledExecutorService.scheduleAtFixedRate(refreshTask, 900, 600, TimeUnit.SECONDS);

        triggerTimeFuture = scheduledExecutorService.scheduleAtFixedRate(triggerTimeTask, TimeSensitive.nextHour(), 3600000, TimeUnit.MILLISECONDS);

        LOG.info(getName() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        for (Map.Entry<String, Partitions> entry : partitionsMap.entrySet()) {
            entry.getValue().stop();
        }
        LOG.info(getName() + " stoped");
        checkFuture.cancel(true);
        refreshFuture.cancel(true);
        triggerTimeFuture.cancel(true);
    }

    @Override
    protected void doClose() throws SearchException {
        for (Map.Entry<String, Partitions> entry : partitionsMap.entrySet()) {
            entry.getValue().close();
        }
        partitionsMap.clear();
        LOG.info(getName() + " closed");
        scheduledExecutorService.shutdown();
        tableShardServiceAble.close();
    }

    @Override
    public String getName() {
        return "HDFSDataBlocksService";
    }

    @Override
    public void open(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        assert table != null;
        Partitions partitions = partitionsMap.get(table);
        if (partitions == null) {
            partitions = partitionsFactory.createTimelinePartitions(
                    settings, metaDefine, tableShardServiceAble.getShardInfos(table), shardsFactory
            );
            partitions.start();
            partitionsMap.put(table, partitions);
        }
    }

    @Override
    public void close(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        assert table != null;
        Partitions partitions = partitionsMap.remove(table);
        if (partitions != null) partitions.close();
    }

    @Override
    public void refresh(RefreshContext refreshContext) {
        for (Map.Entry<String, Partitions> entry : partitionsMap.entrySet()) {
            entry.getValue().refresh(refreshContext);
        }
    }

    @Override
    public Iterator<Partitions> iterator() {
        return partitionsMap.values().iterator();
    }

    public boolean check() {
        LOG.info("------------------start check partitions----------------------");
        for (Map.Entry<String, Partitions> entry : partitionsMap.entrySet()) {
            Partitions partitions = entry.getValue();
            try {
                partitions.check();
            } catch (Exception e) {
                //todo 报警
                LOG.error("partitions check error", e);
            }
        }
        LOG.info("----------------------check partitions finish------------------------");
        return true;
    }

    class PartitionsCheckTask extends TimerTask {

        @Override
        public void run() {
            check();
        }
    }

    class PartitionsRefreshTask extends TimerTask {

        private RefreshContext context;

        PartitionsRefreshTask() {
            context = RefreshContext.buildForAllTable(false);
        }

        @Override
        public void run() {
            refresh(context);
        }
    }

    class TriggerTimeTask extends TimerTask {

        @Override
        public void run() {
            PartitionCycle cycle = new PartitionCycle(System.currentTimeMillis(), PartitionGrain.HOUR);
            for (Map.Entry<String, Partitions> entry : partitionsMap.entrySet()) {
                Partitions partitions = entry.getValue();
                try {
                    partitions.triggerTimeSensitiveEvent(cycle);
                } catch (IOException e) {
                    //todo 报警
                    LOG.error("partitions trigger time sensitive event error", e);
                }
            }
        }
    }

}
