package com.huya.search.node.sep;

import com.google.inject.Singleton;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.rpc.*;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
@Singleton
public abstract class RpcSalverNodeService extends SalverNodeService {

    private SubscriberService subscriberService;

    private ZooKeeperOperator zo;

    private NettyServer rpcServer;

    private SalverRpcProtocol impl;


    public RpcSalverNodeService(SubscriberService subscriberService, ZooKeeperOperator zo) {
        this.subscriberService = subscriberService;
        this.zo = zo;
    }

    @Override
    protected void startRpcService() {
        impl = SalverRpcProtocolImpl.newInstance(subscriberService, zo);

        rpcServer = new NettyServer(
                new SpecificResponder(SalverRpcProtocol.class, impl),
                new InetSocketAddress(zo.getServicePort()),
                new NioServerSocketChannelFactory
                        (Executors.newCachedThreadPool(), Executors.newCachedThreadPool()),
                new ExecutionHandler(Executors.newCachedThreadPool()));
        rpcServer.start();

        LOG.info("rpc service started: " + zo.getServiceUrl());

    }

    @Override
    protected void closeRpcService() {
        rpcServer.close();
    }

}
