package com.huya.search.index.block;

import com.huya.search.node.salvers.SalverNode;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/9.
 */
public interface SalversModListener {

    /**
     * 增加节点监听回调
     * @param salverNode 节点
     */
    void afterAddSalver(SalverNode salverNode);

    /**
     * 丢失节点监听回调
     * @param salverNode 节点
     */
    void afterLostSalver(SalverNode salverNode);

    /**
     * 移除节点监听回调
     * @param salverNode 节点
     */
    void afterRemoveSalver(SalverNode salverNode);
}
