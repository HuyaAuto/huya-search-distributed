package com.huya.search.index.node.rpc;

import com.huya.search.rpc.SalverRpcProtocol;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/29.
 */
public class Master {

    private SalverRpcProtocol proxy;

    public Master() throws IOException {

        NettyTransceiver rpcClient = new NettyTransceiver(
                new InetSocketAddress("127.0.0.1", 11120),
                NettyTransceiver.DEFAULT_CONNECTION_TIMEOUT_MILLIS * 3
        );

        proxy = SpecificRequestor.getClient(SalverRpcProtocol.class, rpcClient);
    }



    public void sql() throws AvroRemoteException {
        proxy.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Salver salver = new Salver();
        Master master = new Master();
        new Thread("one") {
            @Override
            public void run() {
                try {
                    master.sql();
                } catch (AvroRemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        System.out.println("mid");
        new Thread("two") {
            @Override
            public void run() {
                try {
                    master.sql();
                } catch (AvroRemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread("three") {
            @Override
            public void run() {
                try {
                    master.sql();
                } catch (AvroRemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        System.out.println("finish");
        new CountDownLatch(1).await();
    }
}
