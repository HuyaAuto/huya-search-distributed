package com.huya.search.node.sep;

import com.huya.search.node.ZooKeeperOperator;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public abstract class RpcMasterNodeService extends MasterNodeService {

    private ZooKeeperOperator zo;


    public RpcMasterNodeService(ZooKeeperOperator zo) {
        this.zo = zo;
    }

    public ZooKeeperOperator getZo() {
        return zo;
    }
}
