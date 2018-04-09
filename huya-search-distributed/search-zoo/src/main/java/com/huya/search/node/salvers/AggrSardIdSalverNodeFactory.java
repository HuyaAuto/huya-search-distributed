package com.huya.search.node.salvers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.partition.AggrByShardIdPartitionShard;
import com.huya.search.partition.PartitionShard;
import com.huya.search.partition.PartitionShardFactory;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
@Singleton
public class AggrSardIdSalverNodeFactory implements SalverNodeFactory {

    private static final AggrShardIdPartitionShardFactory factory = new AggrShardIdPartitionShardFactory();

    private Settings settings;

    private int virtualNodeNum;

    @Inject
    public AggrSardIdSalverNodeFactory(@Named("Node")Settings settings) {
        this.settings = settings;
        this.virtualNodeNum = settings.getAsInt("virtualNodeNum", 3);
    }

    @Override
    public SalverNode create(NodeBaseEntry nodeBaseEntry) {
        return RealSalverNode.newInstance(nodeBaseEntry, factory, virtualNodeNum);
    }

    private static class AggrShardIdPartitionShardFactory implements PartitionShardFactory {
        @Override
        public PartitionShard create() {
            return AggrByShardIdPartitionShard.newInstance();
        }
    }

}
