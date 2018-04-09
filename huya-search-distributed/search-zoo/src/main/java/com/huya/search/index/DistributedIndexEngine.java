package com.huya.search.index;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.index.block.DisDataBlockManager;
import com.huya.search.index.block.SalversModListener;
import com.huya.search.index.opeation.DisIndexContext;
import com.huya.search.index.opeation.DisShardIndexContext;
import com.huya.search.node.salvers.Salvers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
@Singleton
public class DistributedIndexEngine extends DistributedEngine {

    private Logger LOG = LoggerFactory.getLogger(DistributedIndexEngine.class);

    private final DisDataBlockManager disDataBlockManager;

    @Inject
    public DistributedIndexEngine(DisDataBlockManager disDataBlockManager) {
        this.disDataBlockManager = disDataBlockManager;
    }

    @Override
    public void put(DisIndexContext disIndexContext) throws SearchException {
        this.disDataBlockManager.insert(disIndexContext);
    }

    @Override
    public void remove(DisIndexContext disIndexContext) throws SearchException {
        this.disDataBlockManager.stopInsert(disIndexContext);
    }

    @Override
    public void putFromEnd(DisIndexContext disIndexContext) throws SearchException {
        this.disDataBlockManager.insertFromEnd(disIndexContext);
    }

    @Override
    public void put(DisShardIndexContext disShardIndexContext) throws SearchException {
        this.disDataBlockManager.insertShard(disShardIndexContext);
    }

    @Override
    public void remove(DisShardIndexContext disShardIndexContext) throws SearchException {
        this.disDataBlockManager.stopInsertShard(disShardIndexContext);
    }

    @Override
    public Salvers getAllSalvers() {
        return this.disDataBlockManager.getAllSalvers();
    }

    @Override
    public void addSalversModListener(SalversModListener salversModListener) {
        this.disDataBlockManager.addSalversModListener(salversModListener);
    }

    @Override
    protected void doStart() throws SearchException {
        disDataBlockManager.start();
        LOG.info(getName() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        disDataBlockManager.stop();
        LOG.info(getName() + " stoped");
    }

    @Override
    protected void doClose() throws SearchException {
        disDataBlockManager.close();
        LOG.info(getName() + " closed");
    }

    @Override
    public String getName() {
        return "DistributedIndexEngine";
    }


}
