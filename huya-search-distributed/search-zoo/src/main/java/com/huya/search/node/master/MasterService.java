package com.huya.search.node.master;

import com.huya.search.node.NodeBaseEntry;
import com.huya.search.rpc.RpcServerUp;
import com.huya.search.service.AbstractOrderService;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
public abstract class MasterService extends AbstractOrderService implements RpcServerUp {

    public abstract void quitMaster() throws IOException;

    public abstract boolean isMaster();

    public abstract NodeBaseEntry getMaster() throws Exception;

}
