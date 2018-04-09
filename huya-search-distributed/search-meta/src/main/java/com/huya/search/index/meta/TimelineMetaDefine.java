package com.huya.search.index.meta;

import com.huya.search.partition.PartitionCycle;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/10.
 */
public interface TimelineMetaDefine extends MetaDefine, MetaHeadInfo {

    boolean isOpen();

    void open();

    void close();

    IntactMetaDefine get(long unixTime);

    IntactMetaDefine getLast();

    IntactMetaDefine getFirst();

    IndexFeatureType getIndexFieldFeatureType(String name, long unixTime);

    boolean exists(MetaDefine metaDefine);

    ShardMetaDefine getMetaDefineByCycle(PartitionCycle cycle, int shardId);

    ShardMetaDefine getMetaDefineByUnixTime(long unixTime, int shardId);

    void add(MetaDefine metaDefine);

    List<PartitionCycle> getLastCycle(int n);

    Map<Long, MetaDefine> getTreeMap();
}
