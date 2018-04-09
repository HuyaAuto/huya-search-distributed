package com.huya.search.index.node.rpc;

import com.huya.search.rpc.RpcRefreshContext;
import com.huya.search.rpc.RpcResult;
import com.huya.search.rpc.SalverRpcProtocol;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/29.
 */
public class Salver {

    private static final Logger LOG = LoggerFactory.getLogger(Salver.class);

    private NettyServer rpcServer;

    private ServerImpl server = new ServerImpl();

    public Salver() throws IOException {
//        rpcServer = new HttpServer(
//                new SpecificResponder(SalverRpcProtocol.PROTOCOL, server),
//                11120
//        );

        rpcServer = new NettyServer(
                new SpecificResponder(SalverRpcProtocol.class, server),
                new InetSocketAddress(11120),
                new NioServerSocketChannelFactory
                (Executors.newFixedThreadPool(2), Executors.newFixedThreadPool(5)),
                new ExecutionHandler(Executors.newCachedThreadPool()));
        rpcServer.start();
    }


    static class ServerImpl implements SalverRpcProtocol {

        private static int num = 0;

        @Override
        public Void refresh(RpcRefreshContext refreshContext) throws AvroRemoteException {
            return null;
        }

        @Override
        public RpcResult sql(ByteBuffer queryBytes, List<Integer> shardArray, int shardNum) throws AvroRemoteException {
            System.out.println("test");
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new RpcResult();
        }

        @Override
        public CharSequence syncPullTask() throws AvroRemoteException {
            return null;
        }

        @Override
        public Void openPullTask(CharSequence serviceUrl, CharSequence table, int shardId, CharSequence method) throws AvroRemoteException {
            return null;
        }

        @Override
        public Void closePullTask(CharSequence serviceUrl, CharSequence table, int shardId) throws AvroRemoteException {
            return null;
        }

        @Override
        public Void closePullTaskList(CharSequence serviceUrl, CharSequence table, List<Integer> shardIds) throws AvroRemoteException {
            return null;
        }

        @Override
        public CharSequence insertStat() throws AvroRemoteException {
            return null;
        }

        @Override
        public Void shutdown() throws AvroRemoteException {
            System.out.println("test");
            if (num != 0) {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                num++;
            }
            return null;
        }
    }
}
