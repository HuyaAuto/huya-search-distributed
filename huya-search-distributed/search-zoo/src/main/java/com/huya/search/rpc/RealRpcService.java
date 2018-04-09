package com.huya.search.rpc;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.index.DisKryoSerializerInit;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.DisRefreshContext;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.meta.MasterMetaService;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.tasks.MasterTasksService;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/13.
 */
@Singleton
public class RealRpcService extends RpcService {

    private static final Logger LOG = LoggerFactory.getLogger(RealRpcService.class);

    private ZooKeeperOperator zo;

    private MasterMetaService masterMetaService;
    private MasterTasksService masterTasksService;

    private volatile boolean isMaster;

    private RpcProtocol rpcProtocol;

    private NettyServer rpcServer;

    private NettyTransceiver rpcClient;

    private RpcProtocol proxy;

    private volatile NodeBaseEntry masterEntry;

    @Inject
    public RealRpcService(ZooKeeperOperator zo, MasterMetaService masterMetaService, MasterTasksService masterTasksService, SubscriberService subscriberService) {
        this.zo = zo;
        this.masterMetaService = masterMetaService;
        this.masterTasksService = masterTasksService;
        this.rpcProtocol = RpcProtocolImpl.newInstance(this, masterMetaService, masterTasksService, subscriberService);
    }

    @Override
    public void serverUp() {
        DisKryoSerializerInit.run();
        rpcServer = new NettyServer(
                new SpecificResponder(RpcProtocol.class, rpcProtocol),
                new InetSocketAddress(zo.getServicePort()));
        rpcServer.start();
    }

    @Override
    protected void doStart() throws SearchException {
        while (this.masterEntry == null) {
            try {
                this.masterEntry = masterLatch.getMaster();
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {
                throw new SearchException("rpc get master error", e);
            }
        }
        initRpcClient();
    }

    private void initRpcClient() {
        if (rpcClient != null && rpcClient.isConnected()) {
            rpcClient.close();
        }
        try {
            LOG.info("create client rpc to access {}", masterEntry);
            rpcClient = new NettyTransceiver(
                    new InetSocketAddress(masterEntry.getServiceHost(), masterEntry.getServicePort()),
                    NettyTransceiver.DEFAULT_CONNECTION_TIMEOUT_MILLIS * 3
            );
            proxy = SpecificRequestor.getClient(RpcProtocol.class, rpcClient);
            LOG.info("rpc service started: " + zo.getServiceUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        rpcServer.close();
        rpcClient.close();
    }

    @Override
    public String getName() {
        return "RealRpcService";
    }

//    private void checkMasterChange() {
//        NodeBaseEntry newMasterEntry = adapter.getMaster();
//        if (newMasterEntry != masterEntry || !newMasterEntry.equals(masterEntry)) {
//            masterEntry = newMasterEntry;
//            initRpcClient();
//        }
//    }

    @Override
    public void createMeta(String metaJson) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.createMeta(metaJson);
        }
        else {
//            checkMasterChange();
            proxy.createMeta(metaJson);
        }
    }

    @Override
    public void removeMeta(String table) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.removeMeta(table);
        }
        else {
//            checkMasterChange();
            proxy.removeMeta(table);
        }
    }

    @Override
    public void updateMeta(String table, String content) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.updateMeta(table, content);
        }
        else {
//            checkMasterChange();
            proxy.updateMeta(table, content);
        }
    }

    @Override
    public void openMeta(String table) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.openMeta(table);
        }
        else {
//            checkMasterChange();
            proxy.openMeta(table);
        }
    }

    @Override
    public void closeMeta(String table) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.closeMeta(table);
        }
        else {
//            checkMasterChange();
            proxy.closeMeta(table);
        }
    }

    @Override
    public String metas() throws Exception {
        if (isMaster) {
            return rpcProtocol.metas().toString();
        }
        else {
//            checkMasterChange();
            return proxy.metas().toString();
        }
    }

    @Override
    public String lastMetas() throws Exception {
        if (isMaster) {
            return rpcProtocol.lastMetas().toString();
        }
        else {
//            checkMasterChange();
            return proxy.lastMetas().toString();
        }
    }

    @Override
    public String meta(String table) throws Exception {
        if (isMaster) {
            return rpcProtocol.meta(table).toString();
        }
        else {
//            checkMasterChange();
            return proxy.meta(table).toString();
        }
    }

    @Override
    public String lastMeta(String table) throws Exception {
        if (isMaster) {
            return rpcProtocol.lastMeta(table).toString();
        }
        else {
//            checkMasterChange();
            return proxy.lastMeta(table).toString();
        }
    }


    @Override
    public void refresh(DisRefreshContext refreshContext) throws AvroRemoteException {
        //todo 实现 refreshContext to rpcRefreshContext
        RpcRefreshContext rpcRefreshContext = null;
        if (isMaster) {
            rpcProtocol.refresh(rpcRefreshContext);
        }
        else {
//            checkMasterChange();
            proxy.refresh(rpcRefreshContext);
        }
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> sql(String sql) throws AvroRemoteException {
        if (isMaster) {
            long start = System.currentTimeMillis();
            List<RpcResultRow> list = rpcProtocol.sqlLine(sql).getResult();
            QueryResult<? extends Iterable<IndexableField>> result =  new QueryResult<>(list);
            long end = System.currentTimeMillis();
            result.setRunTime(end - start);
            return result;
        }
        else {
//            checkMasterChange();
            //todo 序列化
            return new QueryResult<>(proxy.sqlLine(sql).getResult());
        }
    }

    @Override
    public String insertStat() throws AvroRemoteException {
        try {
            return rpcProtocol.insertStat().toString();
        } catch (IOException e) {
            throw new AvroRemoteException(e);
        }
    }

    @Override
    public void openPullTask(NodeBaseEntry nodeBaseEntry, PullContext pullContext) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.openPullTask(nodeBaseEntry.getServiceUrl(), pullContext.getTable(), pullContext.getShardId(), pullContext.getMethod().toString());
        }
        else {
            throw new AvroRemoteException("salvers rpc server is not support openPullTask");
        }
    }

    @Override
    public void closePullTask(NodeBaseEntry nodeBaseEntry, PullContext pullContext) throws AvroRemoteException {
        if (isMaster) {
            rpcProtocol.closePullTask(nodeBaseEntry.getServiceUrl(), pullContext.getTable(), pullContext.getShardId());
        }
        else {
            throw new AvroRemoteException("salvers rpc server is not support closePullTask");
        }
    }

    @Override
    public void shutdown() throws AvroRemoteException {
        rpcProtocol.shutdown();
    }

    public MasterMetaService getMasterMetaService() {
        return masterMetaService;
    }

    public MasterTasksService getMasterTasksService() {
        return masterTasksService;
    }

    @Override
    public NodeBaseEntry getMasterNodeBaseEntry() {
        return masterEntry;
    }

    @Override
    public NodeBaseEntry getCurrentNodeBaseEntry() {
        return zo.getNodeBaseEntry();
    }

    @Override
    public boolean isMaster() {
        return isMaster;
    }

    @Override
    public void asMaster() {
        isMaster = true;
        masterMetaService.start();
        masterTasksService.start();
    }

    @Override
    public void asSalver() {
        isMaster = false;
        if (masterTasksService.started()) masterTasksService.close();
        if (masterMetaService.started()) masterMetaService.close();
    }

    public static void main(String[] args) {
        int port = 28889;

        String otherHostName = args[0];

        RpcProtocol rpcProtocol = new TestRpcProtocolImpl();

        NettyServer server = new NettyServer(
                new SpecificResponder(RpcProtocol.class, rpcProtocol),
                new InetSocketAddress(port));

        server.start();

        NettyTransceiver rpcClient = null;

        while(true) {
            try {
                TimeUnit.SECONDS.sleep(30);

                if (rpcClient == null || !rpcClient.isConnected()) {

                    rpcClient = new NettyTransceiver(
                            new InetSocketAddress(otherHostName, port),
                            NettyTransceiver.DEFAULT_CONNECTION_TIMEOUT_MILLIS * 3
                    );
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
