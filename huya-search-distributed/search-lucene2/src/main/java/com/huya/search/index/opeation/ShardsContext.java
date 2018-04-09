package com.huya.search.index.opeation;

import java.util.Collection;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/14.
 */
public interface ShardsContext {

    Collection<Long> getUnixTimes();

    int getShardId();

    int getShardNum();
}
