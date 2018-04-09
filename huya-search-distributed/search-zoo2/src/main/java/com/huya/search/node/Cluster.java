package com.huya.search.node;

import java.util.List;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public interface Cluster {

    Set<SalverNode> getAllSalvers();

    Set<SalverNode> getSalvers(String table);

    SalverNode getSalverNode(NodeBaseEntry nodeBaseEntry);

    SalverNodesQueryInfo getSalverNodesQueryInfo(String table);

    void addSalver(NodeBaseEntry nodeBaseEntry);

    void lostSalver(NodeBaseEntry nodeBaseEntry);

    List<Integer> getShardIdList(SalverNode salverNode, String table);
}
