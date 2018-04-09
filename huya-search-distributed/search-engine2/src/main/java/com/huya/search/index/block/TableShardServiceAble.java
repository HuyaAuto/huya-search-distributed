package com.huya.search.index.block;

import com.huya.search.service.AbstractLifecycleService;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/11.
 */
public abstract class TableShardServiceAble extends AbstractLifecycleService {

    public abstract ShardInfosService getShardInfos(String table);
}
