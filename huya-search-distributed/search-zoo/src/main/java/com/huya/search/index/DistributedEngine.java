package com.huya.search.index;


import com.huya.search.SearchException;
import com.huya.search.index.block.SalversModListener;
import com.huya.search.index.opeation.DisIndexContext;
import com.huya.search.index.opeation.DisShardIndexContext;
import com.huya.search.node.salvers.Salvers;
import com.huya.search.service.AbstractOrderService;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public abstract class DistributedEngine extends AbstractOrderService {

    public abstract void put(DisIndexContext disIndexContext) throws SearchException;

    public abstract void remove(DisIndexContext DisIndexContext) throws SearchException;

    public abstract void putFromEnd(DisIndexContext disIndexContext) throws SearchException;

    public abstract void put(DisShardIndexContext disShardIndexContext) throws SearchException;

    public abstract void remove(DisShardIndexContext disShardIndexContext) throws SearchException;

    public abstract Salvers getAllSalvers();

    public abstract void addSalversModListener(SalversModListener waitNode);
}
