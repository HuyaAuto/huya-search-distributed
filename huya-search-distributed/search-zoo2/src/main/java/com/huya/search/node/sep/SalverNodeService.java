package com.huya.search.node.sep;

import com.huya.search.SearchException;
import com.huya.search.service.AbstractOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public abstract class SalverNodeService extends AbstractOrderService {

    protected static final Logger LOG = LoggerFactory.getLogger(SalverNodeService.class);

    @Override
    protected void doStart() throws SearchException {
        //0. 注册
        registerNode();

        //1. 打开元数据监听
        listenMeta();

        //2. 初始化RPC服务
        startRpcService();

    }

    protected abstract void registerNode();

    protected abstract void listenMeta();

    protected abstract void startRpcService();

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        //0. 关闭RPC服务
//        closeRpcService();

        //1. 关闭元数据监听
        unListenMeta();

        //2. 取消注册
        unRegisterNode();
    }

    protected abstract void closeRpcService();

    protected abstract void unRegisterNode();

    protected abstract void unListenMeta();

}
