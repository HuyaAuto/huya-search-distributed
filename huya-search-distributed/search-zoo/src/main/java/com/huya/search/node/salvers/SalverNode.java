package com.huya.search.node.salvers;

import com.huya.search.hash.Node;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.tasks.PullTask;

import java.util.List;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/1.
 */
public interface SalverNode extends Node<PullTask> {

    boolean isLost();

    void join();

    void lost();

    NodeBaseEntry getNodeEntry();

    int getVirtualNodeNum();

    int loadShardQuantity();

    int loadShardQuantity(String table);

    int loadShardQuantity(String table, int shardId);

    void add(String table, int shardId, long unixTime);

    void remove(String table);

    void remove(String table, int shardId);

    void remove(String table, int shardId, long unixTime);

    boolean contains(String table);

    boolean contains(String table, int shardId);

    boolean contains(String table, long unixTime);

    boolean contains(String table, int shardId, long unixTime);

    boolean isIdle();

    boolean isIdle(String table);

    boolean isIdle(String table, int shardId);

    List<Integer> getShardIdList(String table);

}
