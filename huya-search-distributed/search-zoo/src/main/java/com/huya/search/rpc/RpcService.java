package com.huya.search.rpc;

import com.huya.search.index.opeation.PullContext;
import com.huya.search.node.DisAccessMedium;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.master.RealMasterService;
import com.huya.search.service.AbstractLifecycleService;
import org.apache.avro.AvroRemoteException;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/13.
 */
public abstract class RpcService extends AbstractLifecycleService implements DisAccessMedium, RpcServerUp  {

    protected RealMasterService.MasterLatch masterLatch;

    public RpcService sync(RealMasterService.MasterLatch masterLatch) {
        this.masterLatch = masterLatch;
        return this;
    }

    public abstract NodeBaseEntry getMasterNodeBaseEntry();

    public abstract NodeBaseEntry getCurrentNodeBaseEntry();

    public abstract boolean isMaster();

    public abstract void asMaster();

    public abstract void asSalver();

    public abstract void openPullTask(NodeBaseEntry nodeBaseEntry, PullContext pullContext) throws AvroRemoteException;

    public abstract void closePullTask(NodeBaseEntry nodeBaseEntry, PullContext pullContext) throws AvroRemoteException;

    public abstract void shutdown() throws AvroRemoteException;
}
