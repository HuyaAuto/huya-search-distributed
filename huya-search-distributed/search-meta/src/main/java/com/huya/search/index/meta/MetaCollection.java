package com.huya.search.index.meta;


import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/10.
 */
public interface MetaCollection {

    Iterator<TimelineMetaDefine> iterator();

    boolean contains(String table);

    boolean remove(String table);

    void put(String table, TimelineMetaDefine metaDefine);

    TimelineMetaDefine get(String table);

    TimelineMetaDefine open(String table);

    TimelineMetaDefine close(String table);

    void clear();

    Set<String> getTables();
}
