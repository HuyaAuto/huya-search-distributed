package com.huya.search.node.salvers;

import com.huya.search.node.NodeBaseEntry;
import com.huya.search.partition.PartitionCycle;

import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/25.
 */
public interface Salvers {

    int salversNum();

    void join(SalverNode salver);

    SalverNode lost(NodeBaseEntry nodeBaseEntry);

    Set<SalverNode> getSalvers();

    Set<SalverNode> getSalvers(String table);

    Set<SalverNode> getSalversWithOut(String table);

    Set<SalverNode> getSalvers(String table, PartitionCycle cycle);

    Set<SalverNode> getSalvers(String table, int shardId);

    Set<SalverNode> getSalvers(String table, long unixTime);

    SalverNode getSalver(NodeBaseEntry nodeBaseEntry);

    SalverNode getSalver(String table, PartitionCycle cycle, int shardId);

    SalverNode getSalver(String table, long unixTime, int shardId);

    Set<SalverNode> getIdleSalvers();

    int getShardNum(String table);

    void pull(String table, int partitionNum);

    void stopPull(String table, int partitionNum);

    void pullShard(String table, int shardId);

    void stopPullShard(String table, int shardId);

    void pullFromEnd(String table, int partitionNum);
}
