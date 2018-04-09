package com.huya.search.facing.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.index.Engine;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.meta.MetaFollower;
import com.huya.search.service.AbstractOrderService;

import java.util.List;
import java.util.Properties;

public abstract class SubscriberService extends AbstractOrderService implements MetaFollower {

    protected abstract Properties getSubscriberSettings();

    public abstract Engine getEngine();

    public abstract void openPullTask(String table, int shardId) throws ShardsOperatorException;

    public abstract void openPullTaskFromEnd(String table, int shardId) throws ShardsOperatorException;

    public abstract void closePullTask(String table, int shardId) throws ShardsOperatorException;

    public abstract void closePullTask(String table, List<Integer> shardIds) throws ShardsOperatorException;

    public abstract JsonNode getInsertStat();

    public abstract void preventDuplicationSeekAnaPollException(ShardsOperatorException e);

    public abstract void putException(ShardsOperatorException e);

    public abstract JsonNode syncPullTask();
}
