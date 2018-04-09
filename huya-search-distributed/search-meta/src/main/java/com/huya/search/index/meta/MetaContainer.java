package com.huya.search.index.meta;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/19.
 */
public interface MetaContainer {

    void add(TimelineMetaDefine metaDefine);

    void remove(String table);

    void refresh(String table);

    void open(String table);

    void close(String table);

    void register(MetaFollower metaFollower);

    TimelineMetaDefine get(String table);

    Iterator<TimelineMetaDefine> iterator();

    Set<String> getTables();

}
