package com.huya.search.index.meta;

import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/19.
 */
public interface MetaSyncAble {

    void add(String timelineMetaDefineJson);

    void remove(String table);

    void update(String table, String metaDefineJson);

    String view(String table);

    Set<String> getTables();

}
