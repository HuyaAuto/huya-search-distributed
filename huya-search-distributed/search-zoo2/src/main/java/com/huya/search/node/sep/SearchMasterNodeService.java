package com.huya.search.node.sep;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.meta.MasterMetaService;
import com.huya.search.meta.SalversMetaService;
import com.huya.search.node.TaskDistributionService;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.rpc.MasterRpcProtocol;
import com.huya.search.rpc.MasterRpcProtocolImpl;


/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
@Singleton
public class SearchMasterNodeService extends RpcMasterNodeService {

    private MasterMetaService masterMetaService;

    private SalversMetaService salversMetaService;

    private TaskDistributionService taskDistributionService;

    private MasterRpcProtocol impl;

    @Inject
    public SearchMasterNodeService(MasterMetaService masterMetaService, SalversMetaService salversMetaService, TaskDistributionService taskDistributionService, ZooKeeperOperator zo) {
        super(zo);
        this.masterMetaService = masterMetaService;
        this.salversMetaService = salversMetaService;
        this.taskDistributionService = taskDistributionService;
    }

    @Override
    protected void listenSalverNode() {
        taskDistributionService.start();
    }

    @Override
    protected void syncAndListenMeta() {
        salversMetaService.start();
        masterMetaService.start();
    }

    @Override
    protected void startRpcService() {
        impl = MasterRpcProtocolImpl.newInstance(masterMetaService, taskDistributionService);
    }

    @Override
    protected void determineAndSyncPullTask() {
        taskDistributionService.determineAndSyncPullTask(impl);
    }

    @Override
    protected void closeSyncPullTask() {
        taskDistributionService.closeSyncPullTask();
    }

    @Override
    protected void closeRpcService() {
        //do nothing
    }

    @Override
    protected void unListenMeta() {
        masterMetaService.close();
        salversMetaService.close();
    }

    @Override
    protected void unListenSalverNode() {
        taskDistributionService.close();
    }

    @Override
    public MasterRpcProtocol getMasterRpcProtocol() {
        return impl;
    }

    @Override
    public String getName() {
        return "SearchMasterNodeService";
    }
}
