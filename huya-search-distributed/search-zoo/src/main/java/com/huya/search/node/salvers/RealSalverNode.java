package com.huya.search.node.salvers;

import com.github.ssedano.hash.JumpConsistentHash;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.partition.PartitionShard;
import com.huya.search.partition.PartitionShardFactory;
import com.huya.search.tasks.PullTask;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
public class RealSalverNode implements SalverNode {

    private NodeBaseEntry nodeEntry;

    private Map<String, PartitionShard> dataDisMap = new HashMap<>();

    private PartitionShardFactory factory;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private int virtualNodeNum;

    private List<Integer> hashCache = new ArrayList<>();

    private List<Set<PullTask>> pullSetList;

    private volatile boolean lost = false;

    public static RealSalverNode newInstance(NodeBaseEntry nodeEntry, PartitionShardFactory factory, int virtualNodeNum) {
        return new RealSalverNode(nodeEntry, factory, virtualNodeNum);
    }

    private RealSalverNode(NodeBaseEntry nodeEntry, PartitionShardFactory factory, int virtualNodeNum) {
        this.nodeEntry = nodeEntry;
        this.factory = factory;
        this.virtualNodeNum = virtualNodeNum;
        initPullSetList();
    }

    private void initPullSetList() {
        pullSetList = new ArrayList<>(this.virtualNodeNum);
        for (int i = 0; i < this.virtualNodeNum; i++) {
            pullSetList.add(new HashSet<>());
        }
    }

    @Override
    public boolean isLost() {
        return lost;
    }

    @Override
    public void join() {
        lost = false;
    }

    @Override
    public void lost() {
        lost = true;
    }

    @Override
    public NodeBaseEntry getNodeEntry() {
        return nodeEntry;
    }

    @Override
    public int getVirtualNodeNum() {
        return virtualNodeNum;
    }

    @Override
    public int loadShardQuantity() {
        lock.readLock().lock();
        try {
            return dataDisMap.values().stream().mapToInt(PartitionShard::loadQuantity).sum();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int loadShardQuantity(String table) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard == null ? 0 : partitionShard.loadQuantity();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int loadShardQuantity(String table, int shardId) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard == null ? 0 : partitionShard.loadShardQuantity(shardId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void add(String table, int shardId, long unixTime) {
        lock.writeLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            if (partitionShard == null) {
                partitionShard = factory.create();
                dataDisMap.put(table, partitionShard);
            }
            partitionShard.add(unixTime, shardId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(String table) {
        lock.writeLock().lock();
        try {
            dataDisMap.remove(table);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(String table, int shardId) {
        lock.writeLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            if (partitionShard != null) {
                partitionShard.remove(shardId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(String table, int shardId, long unixTime) {
        lock.writeLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            if (partitionShard != null) {
                partitionShard.remove(unixTime, shardId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(String table) {
        lock.readLock().lock();
        try {
            return dataDisMap.containsKey(table);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(String table, int shardId) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard != null && partitionShard.contains(shardId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(String table, long unixTime) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard != null && partitionShard.contains(unixTime);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(String table, int shardId, long unixTime) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard != null && partitionShard.contains(unixTime, shardId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isIdle() {
        lock.readLock().lock();
        try {
            return dataDisMap.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isIdle(String table) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard.isIdle();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isIdle(String table, int shardId) {
        lock.readLock().lock();
        try {
            PartitionShard partitionShard = dataDisMap.get(table);
            return partitionShard.isIdle(shardId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Integer> getShardIdList(String table) {
        List<Integer> integers = new ArrayList<>();
        pullSetList.forEach(pullTasks -> {
            pullTasks.forEach(pullTask -> {
                PullContext pullContext = pullTask.getPullContext();
                if (pullContext.getTable().equals(table)) {
                    integers.add(pullContext.getShardId());
                }
            });
        });
        return integers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealSalverNode that = (RealSalverNode) o;

        return nodeEntry.equals(that.nodeEntry);
    }

    @Override
    public int hashCode() {
        return nodeEntry.hashCode();
    }

    @Override
    public int hash(int i) {
        if (hashCache.size() == 0) {
            initHash();
        }
        return hashCache.get(i);
    }

    @Override
    public void add(int id, PullTask assignObject) {
        pullSetList.get(id).add(assignObject);
    }

    @Override
    public void addAll(int id, Collection<PullTask> handOverSet) {
        pullSetList.get(id).addAll(handOverSet);
    }

    @Override
    public void remove(int id, PullTask assignObject) {
        pullSetList.get(id).remove(assignObject);
    }

    @Override
    public void removeAll(int id, Collection<PullTask> handOverSet) {
        pullSetList.get(id).removeAll(handOverSet);
    }

    @Override
    public Set<PullTask> getAllAssignObject() {
        Set<PullTask> pullTaskSet = new HashSet<>();
        pullSetList.forEach(pullTaskSet::addAll);
        return pullTaskSet;
    }

    @Override
    public Set<PullTask> getAssignObject(int id) {
        return pullSetList.get(id);
    }


    private void initHash() {
        for (int i = 0; i < virtualNodeNum; i++) {
            hashCache.add(JumpConsistentHash.jumpConsistentHash(nodeEntry.getServiceUrl() + "#" + i, Integer.MAX_VALUE));
        }
    }
}
