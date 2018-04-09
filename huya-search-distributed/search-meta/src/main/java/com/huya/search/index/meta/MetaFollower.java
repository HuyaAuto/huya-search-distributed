package com.huya.search.index.meta;

public interface MetaFollower {

    int priority();

    void open(TimelineMetaDefine metaDefine);

    void close(TimelineMetaDefine metaDefine);
}
