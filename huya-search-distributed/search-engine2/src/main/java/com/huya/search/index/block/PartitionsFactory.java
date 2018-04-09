package com.huya.search.index.block;

import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public interface PartitionsFactory {

    Partitions createTimelinePartitions(Settings settings, TimelineMetaDefine timelineMetaDefine,
                                        ShardInfosService shardInfosService, ShardsFactory shardsFactory);
}
