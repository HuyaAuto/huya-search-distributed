package com.huya.search.node.sep;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.meta.SalversMetaService;
import com.huya.search.node.RegisterService;
import com.huya.search.node.ZooKeeperOperator;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
@Singleton
public class SearchSalverNodeService extends RpcSalverNodeService {

    private SalversMetaService salversMetaService;

    private RegisterService registerService;

    @Inject
    public SearchSalverNodeService(SalversMetaService salversMetaService, RegisterService registerService,
                                   SubscriberService subscriberService, ZooKeeperOperator zo) {
        super(subscriberService, zo);
        this.salversMetaService = salversMetaService;
        this.registerService = registerService;
    }

    @Override
    protected void registerNode() {
        registerService.registerNode();
    }

    @Override
    protected void listenMeta() {
        salversMetaService.start();
    }

    @Override
    protected void unRegisterNode() {
        registerService.unRegisterNode();
    }

    @Override
    protected void unListenMeta() {
        salversMetaService.close();
    }

    @Override
    public String getName() {
        return "SearchSalverNodeService";
    }

}
