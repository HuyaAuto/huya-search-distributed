package com.huya.search.node.sep;

import com.huya.search.SearchException;
import com.huya.search.rpc.MasterRpcProtocol;
import com.huya.search.service.AbstractOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public abstract class MasterNodeService extends AbstractOrderService {

    protected static final Logger LOG = LoggerFactory.getLogger(MasterNodeService.class);

    @Override
    protected void doStart() throws SearchException {
        //0. 同步与监听元数据
        syncAndListenMeta();

        //1. 监听子节点
        listenSalverNode();

        //2. 初始化RPC服务
        startRpcService();

        //3. 确认与同步子节点任务状态
        determineAndSyncPullTask();
    }

    protected abstract void listenSalverNode();

    protected abstract void syncAndListenMeta();

    protected abstract void startRpcService();

    protected abstract void determineAndSyncPullTask();


    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        //0. 关闭同步节点任务状态
        closeSyncPullTask();

        //1. 关闭RPC服务
        closeRpcService();

        //2. 关闭子节点监听
        unListenSalverNode();

        //3. 关闭元数据监听
        unListenMeta();
    }

    protected abstract void closeSyncPullTask();

    protected abstract void closeRpcService();

    protected abstract void unListenMeta();

    protected abstract void unListenSalverNode();

    public abstract MasterRpcProtocol getMasterRpcProtocol();

}
