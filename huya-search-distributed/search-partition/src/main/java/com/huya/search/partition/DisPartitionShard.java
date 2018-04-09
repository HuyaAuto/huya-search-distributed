package com.huya.search.partition;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/1.
 *
 * 非线程安全上层需要加锁
 *
 * 表的分片完全分散在各个节点的实现
 */
public class DisPartitionShard implements PartitionShard {

    private Map<Long, BitSet> disMap = new HashMap<>();

    private int loadQuantity = 0;

    public static PartitionShard newInstance() {
        return new DisPartitionShard();
    }

    private DisPartitionShard() {}

    public void add(long unixTime, int shardId) {
        BitSet bitSet = disMap.get(unixTime);
        if (bitSet == null) {
            bitSet = new BitSet();
            disMap.put(unixTime, bitSet);
        }
        if (!bitSet.get(shardId)) {
            bitSet.set(shardId, true);
            loadQuantity++;
        }
    }

    public void remove(long unixTime) {
        BitSet bitSet = disMap.remove(unixTime);
        if (bitSet != null) {
            loadQuantity -= bitSet.cardinality();
        }
    }

    public void remove(long unixTime, int shardId) {
        BitSet bitSet = disMap.get(unixTime);
        if (bitSet != null) {
            if (bitSet.get(shardId)) {
                bitSet.set(shardId, false);
                loadQuantity --;
                if (bitSet.isEmpty()) {
                    disMap.remove(unixTime);
                }
            }
        }
    }

    @Override
    public void remove(int shardId) {
        List<Long> unixTimeList = new ArrayList<>();
        disMap.forEach((unixTime, bitSet) -> {
            if (bitSet.get(shardId)) {
                bitSet.set(shardId, false);
                loadQuantity --;
                if (bitSet.isEmpty()) {
                    unixTimeList.add(unixTime);
                }
            }
        });
        unixTimeList.forEach(unixTime -> disMap.remove(unixTime));
    }

    @Override
    public boolean contains(int shardId) {
        return disMap.values().stream().anyMatch(bitSet -> bitSet.get(shardId));
    }

    public boolean contains(long unixTime) {
        return disMap.containsKey(unixTime);
    }

    public boolean contains(long unixTime, int shardId) {
        BitSet bitSet = disMap.get(unixTime);
        return bitSet != null && bitSet.get(shardId);
    }

    public int loadQuantity() {
        return loadQuantity;
    }

    @Override
    public int loadShardQuantity(int shardId) {
        return disMap.values().stream().mapToInt(bitSet -> bitSet.get(shardId) ? 1 : 0).sum();
    }

    @Override
    public boolean isIdle() {
        return loadQuantity == 0;
    }

    @Override
    public boolean isIdle(int shardId) {
        return loadShardQuantity(shardId) == 0;
    }


}
