package com.huya.search.node;

import com.huya.search.node.master.MasterService;
import com.huya.search.node.salvers.SalversService;
import com.huya.search.service.AbstractOrderService;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/16.
 */
public abstract class NodeService extends AbstractOrderService {

    MasterService masterService;

    SalversService salversService;

    private NodeEntry nodeEntry;

    NodeService(MasterService masterService, SalversService salversService, NodeEntry nodeEntry) {
        this.masterService  = masterService;
        this.salversService = salversService;
        this.nodeEntry = nodeEntry;
    }

    public MasterService getMasterService() {
        return masterService;
    }

    public SalversService getSalversService() {
        return salversService;
    }

    public NodeEntry getNodeEntry() {
        return nodeEntry;
    }

    public String getServiceIP() {
        return nodeEntry.getServiceHost();
    }

    public int getServicePort() {
        return nodeEntry.getServicePort();
    }

    public String getServiceUrl() {
        return nodeEntry.getServiceUrl();
    }


}
