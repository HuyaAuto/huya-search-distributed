package com.huya.search.node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.node.master.MasterService;
import com.huya.search.node.salvers.SalversService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/16.
 */
@Singleton
public class RealNodeService extends NodeService {

    private static final Logger LOG = LoggerFactory.getLogger(RealNodeService.class);

    @Inject
    public RealNodeService(MasterService masterService, SalversService salversService, NodeEntry nodeEntry) {
        super(masterService, salversService, nodeEntry);
    }

    @Override
    protected void doStart() throws SearchException {
        //在注册节点前确保 RPC SERVER 段已经启动（防止 Salver 启动后不能及时接受 MASTER 的指挥的情况）
        masterService.serverUp();

        salversService.start();

        masterService.start();
        LOG.info("node started");
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        masterService.close();
        salversService.close();
        LOG.info("node stopped");
    }

    @Override
    public String getName() {
        return "RealNodeService";
    }

}
