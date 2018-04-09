package com.huya.search.index.meta;


import com.fasterxml.jackson.databind.node.ObjectNode;

public interface MetaHandle {

    MetaCollection load();

    void unLoad();

    TimelineMetaDefine createTimelineMetaDefine(ObjectNode objectNode);

    MetaDefine createMetaDefine(TimelineMetaDefine metaDefine, ObjectNode objectNode);

    void persistence(TimelineMetaDefine metaDefine);

    void remove(String table);

    void update(String table, MetaDefine metaDefine);
}
