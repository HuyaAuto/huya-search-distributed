package com.huya.search.partition;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 *
 * 非线程安全上层需要加锁
 *
 * 一个表的分片按照分片号聚在一个节点的实现
 */
public class AggrByShardIdPartitionShard implements PartitionShard {

    private Map<Integer, UnixTimeShard> disMap = new HashMap<>();

    public static PartitionShard newInstance() {
        return new AggrByShardIdPartitionShard();
    }

    private AggrByShardIdPartitionShard() {}

    @Override
    public void add(long unixTime, int shardId) {
        UnixTimeShard unixTimeShard = disMap.get(shardId);
        if (unixTimeShard == null) {
            unixTimeShard = UnixTimeShard.newInstance(shardId);
        }
        unixTimeShard.add(unixTime);
    }

    @Override
    public void remove(long unixTime) {
        disMap.values().forEach(unixTimeShard -> unixTimeShard.remove(unixTime));
    }

    @Override
    public void remove(long unixTime, int shardId) {
        UnixTimeShard unixTimeShard = disMap.get(shardId);
        if (unixTimeShard != null) {
            unixTimeShard.remove(unixTime);
        }
    }

    @Override
    public void remove(int shardId) {
        disMap.remove(shardId);
    }

    @Override
    public boolean contains(int shardId) {
        return disMap.containsKey(shardId);
    }

    @Override
    public boolean contains(long unixTime) {
        return disMap.values().stream().anyMatch(unixTimeShard -> unixTimeShard.contains(unixTime));
    }

    @Override
    public boolean contains(long unixTime, int shardId) {
        UnixTimeShard unixTimeShard = disMap.get(shardId);
        return unixTimeShard != null && unixTimeShard.contains(unixTime);
    }

    @Override
    public int loadQuantity() {
        return disMap.values().stream().mapToInt(UnixTimeShard::loadQuantity).sum();
    }

    @Override
    public int loadShardQuantity(int shardId) {
        UnixTimeShard unixTimeShard = disMap.get(shardId);
        if (unixTimeShard != null) {
            return unixTimeShard.loadQuantity();
        }
        return 0;
    }

    @Override
    public boolean isIdle() {
        return loadQuantity() == 0;
    }

    @Override
    public boolean isIdle(int shardId) {
        return loadShardQuantity(shardId) == 0;
    }

    private static class UnixTimeShard {

        private int shardId;

        private Set<Long> partitionSet = new HashSet<>();

        public static UnixTimeShard newInstance(int shardId) {
            return new UnixTimeShard(shardId);
        }

        private UnixTimeShard(int shardId) {
            this.shardId = shardId;
        }

        public int getShardId() {
            return shardId;
        }

        public void add(long unixTime) {
            partitionSet.add(unixTime);
        }

        public void remove(long unixTime) {
            partitionSet.remove(unixTime);
        }


        public boolean contains(long unixTime) {
            return partitionSet.contains(unixTime);
        }

        public int loadQuantity() {
            return partitionSet.size();
        }
    }

}
