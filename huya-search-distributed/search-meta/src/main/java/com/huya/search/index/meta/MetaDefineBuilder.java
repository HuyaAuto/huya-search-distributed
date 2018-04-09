package com.huya.search.index.meta;

import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;

public class MetaDefineBuilder {

    public static MetaDefineBuilder newInstance() {
        return new MetaDefineBuilder();
    }

    private MetaDefineBuilder() {}

    public MetaDefine build(IndexMapBuilder indexMapBuilder, String timestampKey) {
        PartitionCycle cycle = new PartitionCycle(timestampKey);
        return new RealMetaDefine(cycle, indexMapBuilder.build());
    }

    public MetaDefine build(IndexMapBuilder indexMapBuilder, PartitionGrain grain, long unixTime) {
        PartitionCycle cycle = new PartitionCycle(unixTime, grain);
        return new RealMetaDefine(cycle, indexMapBuilder.build());
    }
}
