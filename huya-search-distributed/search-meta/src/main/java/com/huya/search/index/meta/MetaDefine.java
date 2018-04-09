package com.huya.search.index.meta;

import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;

import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public interface MetaDefine extends JsonObjectAble {

    IndexFeatureType getIndexFieldFeatureType(String name);

    IndexFeatureType defaultIndexFieldFeatureType();

    TimelineMetaDefine tran(MetaHeadInfo metaHeadInfo);

    PartitionGrain getGrain();

    Map<String, IndexFeatureType> getMap();

    Map<String, IndexFeatureType> getAll();

    PartitionCycle getFirstPartitionCycle();

    String getFirstTimestamp();

    String getMetaDefineTimestampKey();

}
