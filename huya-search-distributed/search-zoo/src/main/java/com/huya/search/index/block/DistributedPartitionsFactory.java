package com.huya.search.index.block;

import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public class DistributedPartitionsFactory implements PartitionsFactory {

    @Override
    public Partitions createTimelinePartitions(Settings settings, TimelineMetaDefine timelineMetaDefine, ShardInfosService shardInfosService) {
        return new DistributedIndexPartitions(settings, timelineMetaDefine, shardInfosService);
    }
}
