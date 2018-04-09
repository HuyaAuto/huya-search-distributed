package com.huya.search.index.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huya.search.SearchException;
import com.huya.search.index.lucene.*;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.*;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.settings.Settings;
import com.huya.search.util.JsonUtil;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/30.
 */
public class IndexPartitions extends Partitions {

    private PartitionGrain partitionGrain;

    private ShardInfos shardInfos;  //todo 可能以后会增加更多信息

    private Vector<Shards> shardsVector;

    private ShardsFactory shardsFactory;

    OperatorPool pool;

    TimelineMetaDefine timelineMetaDefine;

    IndexPartitions(Settings settings, TimelineMetaDefine timelineMetaDefine,
                    ShardInfosService shardInfosService, ShardsFactory shardsFactory) {
        super(settings);
        this.timelineMetaDefine = timelineMetaDefine;
        this.partitionGrain     = timelineMetaDefine.getGrain();
        this.shardInfos = shardInfosService.getShardInfos();
        this.shardsFactory = shardsFactory;
    }

    @Override
    public void initShardsVector() {
        int num = shardInfos.shardNum();
        shardsVector = new Vector<>();
        for (int i = 0; i < num; i++) {
            ShardCoordinate shardCoordinate = new ShardCoordinateContext(getTable(), i);
            shardsVector.add(shardsFactory.create(pool, shardCoordinate));
        }
    }

    @Override
    public void openShard(OpenContext openContext) throws ShardsOperatorException {
        int shardId = openContext.getShardId();
        Shards shards = shardsVector.get(shardId);
        if (!shards.isOpen()) {
            shards.open();
        }
    }

    @Override
    public void closeShard(CloseContext closeContext) throws ShardsOperatorException {
        int shardId = closeContext.getShardId();
        Shards shards = shardsVector.get(shardId);
        if (shards.isOpen()) {
            shards.close();

        }
    }

    @Override
    Shard getShard(ShardContext shardContext) throws ShardsOperatorException {
        long floorUnixTime = partitionGrain.getFloor(shardContext.getUnixTime());
        int  shardId       = shardContext.getShardId();
        return getShard(shardId, floorUnixTime);
    }

    private Shard getShard(int shardId, long unixTime) throws ShardsOperatorException {
        return getShards(shardId).getShard(unixTime);
    }

    private Shards getShards(int shardId) {
        return shardsVector.get(shardId);
    }

    @Override
    QueryShards getShards(ShardsContext shardsContext) {
        Shards shards = getShards(shardsContext.getShardId());

        Collection<Long> unixTimes = shardsContext.getUnixTimes();
        return shards.getQueryShards(unixTimes);
    }

    @Override
    public long lastPartitionMaxOffset(RestoreContext restoreContext) throws ShardsOperatorException {
        return getShards(restoreContext.getShardId()).lastPartitionMaxOffset();
    }

    @Override
    public long maxOffset(PartitionRestoreContext partitionRestoreContext) throws ShardsOperatorException {
        Shard shard = getShard(partitionRestoreContext.getShardId(), partitionRestoreContext.getUnixTime());
        return shard.maxOffset();
    }

    @Override
    public boolean exist(ExistContext existContext) throws ShardsOperatorException {
        Shard shard = getShard(existContext.getShardId(), existContext.getUnixTime());
        return shard.exist(existContext.getOffset());
    }



    @Override
    TimelineMetaDefine getTimelineMetaDefine() {
        return timelineMetaDefine;
    }

    @Override
    void check() {
        for (Shards shards : shardsVector) {
            if (shards.isOpen()) {
                shards.check();
            }
        }
    }

    @Override
    public void refresh(RefreshContext refreshContext) {
        for (Shards shards : shardsVector) {
            if (shards.isOpen()) {
                shards.refresh(refreshContext);
            }
        }
    }


    @Override
    public void triggerTimeSensitiveEvent(PartitionCycle cycle) {
        for (Shards shards : shardsVector) {
            if (shards.isOpen()) {
                shards.triggerTimeSensitiveEvent(cycle);
            }
        }
    }

    @Override
    public Iterator<Shards> iterator() {
        return shardsVector.stream().filter(Shards::isOpen).iterator();
    }

    @Override
    public JsonNode insertStat() {
        ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
        for (Shards shards : this) {
            arrayNode.add(shards.insertStat());
        }
        return arrayNode;
    }

    @Override
    protected void doStart() throws SearchException {
        if (pool == null) {
            this.pool = ModulesBuilder.getInstance()
                    .createInjector().getInstance(LazyShardLuceneService.class)
                    .getOperatorPool(timelineMetaDefine);
        }
    }

    @Override
    protected void doStop() throws SearchException {
    }

    @Override
    protected void doClose() throws SearchException {
        LOG.info("close partition {}", getTable());
        for (Shards shards : shardsVector) {
            if (shards.isOpen()) {
                try {
                    shards.close();
                } catch (ShardsOperatorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "IndexPartitions";
    }


}
