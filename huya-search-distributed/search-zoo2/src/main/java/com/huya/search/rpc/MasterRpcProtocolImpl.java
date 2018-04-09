package com.huya.search.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.facing.subscriber.KafkaPullContext;
import com.huya.search.index.DisKryoSerializerInit;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.index.opeation.*;
import com.huya.search.meta.MasterMetaService;
import com.huya.search.node.*;
import com.huya.search.node.sep.SyncPullTaskState;
import com.huya.search.util.JsonUtil;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/14.
 */
public class MasterRpcProtocolImpl implements MasterRpcProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(MasterRpcProtocolImpl.class);

    private static final KryoPool KRYO_POOL = new KryoPool.Builder(() -> {
        Kryo kryo = new Kryo();
        DisKryoSerializerInit.run(kryo);
        return kryo;
    }).softReferences().build();

    public static MasterRpcProtocolImpl newInstance(MasterMetaService masterMetaService, TaskDistributionService taskDistributionService) {
        return new MasterRpcProtocolImpl(masterMetaService, taskDistributionService);
    }

    private Map<NodeBaseEntry, SalverRpcProtocol> proxyCache = new HashMap<>();

    private MasterMetaService masterMetaService;

    private TaskDistributionService taskDistributionService;

    private ReentrantLock cacheLock = new ReentrantLock();


    private MasterRpcProtocolImpl(MasterMetaService masterMetaService, TaskDistributionService taskDistributionService) {
        this.masterMetaService = masterMetaService;
        this.taskDistributionService = taskDistributionService;
    }

    @Override
    public void createMeta(String metaJson) {
        TimelineMetaDefine metaDefine = JsonMetaUtil.createTimelineMetaDefineFromStr(metaJson);
        masterMetaService.createMeta(metaDefine);
    }

    @Override
    public void removeMeta(String table) {
        masterMetaService.removeMeta(table);
    }

    @Override
    public void updateMeta(String table, String content) {
        masterMetaService.updateMeta(table, JsonMetaUtil.createMetaDefineFromStr(content));
    }

    @Override
    public void openMeta(String table) {
        //todo openMeta
    }

    @Override
    public void closeMeta(String table) {
        //todo closeMeta
    }

    @Override
    public String meta(String table) {
        TimelineMetaDefine timelineMetaDefine = masterMetaService.meta(table);
        return timelineMetaDefine.toObject().toString();
    }

    @Override
    public String lastMeta(String table) {
        TimelineMetaDefine timelineMetaDefine = masterMetaService.meta(table);
        return timelineMetaDefine.getLast().toObject().toString();
    }

    @Override
    public String metas() {
        Iterator<TimelineMetaDefine> iterator = masterMetaService.metas();
        ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
        while (iterator.hasNext()) {
            arrayNode.add(iterator.next().toObject());
        }
        return arrayNode.toString();
    }

    @Override
    public String lastMetas() {
        Iterator<TimelineMetaDefine> iterator = masterMetaService.metas();
        ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
        while (iterator.hasNext()) {
            arrayNode.add(iterator.next().getLast().toObject());
        }
        return arrayNode.toString();
    }

    @Override
    public Void refresh(RpcRefreshContext refreshContext) {
        //todo refresh
        return null;
    }

    @Override
    public RpcResult sql(ByteBuffer queryBytes, List<Integer> shardArray, int shardNum) throws AvroRemoteException {
        throw new AvroRemoteException("no support sql function");
    }

    @Override
    public CharSequence syncPullTask() throws AvroRemoteException {
        Set<SalverNode> salverNodes = taskDistributionService.getAllSalvers();
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        salverNodes.forEach(salverNode -> {
            String serverUrl = salverNode.getNodeEntry().getServiceUrl();
            addPullTaskToJsonObject(salverNode, serverUrl, objectNode);
        });

        return objectNode.toString();
    }

    @Override
    public String syncPullTask(String serverUrl) throws AvroRemoteException {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        SalverNode salverNode = taskDistributionService.getSalverNode(NodeBaseEntryImpl.newInstance(serverUrl));
        addPullTaskToJsonObject(salverNode, serverUrl, objectNode);
        return objectNode.toString();
    }

    private void addPullTaskToJsonObject(SalverNode salverNode, String serverUrl,  ObjectNode objectNode) {
        if (! salverNode.isLost()) {
            SalverRpcProtocol rpcProtocol = getProxyCache(salverNode.getNodeEntry());
            try {
                objectNode.set(serverUrl, JsonUtil.getObjectMapper().readTree(rpcProtocol.syncPullTask().toString()));
            } catch (IOException e) {
                objectNode.put(serverUrl, SyncPullTaskState.ERROR.name());
                e.printStackTrace();
            }
        }
        else {
            objectNode.put(serverUrl, SyncPullTaskState.LOST.name());
        }
    }

    @Override
    public QueryDetailResult sql(String sql) {
        long start = System.currentTimeMillis();

        DisQueryContext disQueryContext = DisOperationFactory.getQueryContext(sql);

        Output output = new Output(new ByteArrayOutputStream());

        Kryo kryo = KRYO_POOL.borrow();
        kryo.writeClassAndObject(output, disQueryContext);
        ByteBuffer byteBuffer = ByteBuffer.wrap(output.getBuffer());
        KRYO_POOL.release(kryo);

        String table = disQueryContext.getTable();

        SalverNodesQueryInfo queryInfo = taskDistributionService.getSalverNodesQueryInfo(table);

        Set<SalverNode> salverNodes = queryInfo.getNodeSet();

        int shardNum = queryInfo.getShardNum();

        List<Future<RpcResult>> futures = new ArrayList<>(salverNodes.size());

        salverNodes.forEach(salverNode -> {

            List<Integer> integers = taskDistributionService.getShardIdList(salverNode, table);
            SalverRpcProtocol rpcProtocol = getProxyCache(salverNode.getNodeEntry());
            futures.add(ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> rpcProtocol.sql(byteBuffer, integers, shardNum)));

        });

        RpcResult rpcResult = disQueryContext.getNodeMerger().merger(futures);

        long end = System.currentTimeMillis();

        return new QueryDetailResult().setRpcResult(rpcResult)
                .setTable(table)
                .setHitNode(salverNodes.size())
                .setHitShard(shardNum)
                .setUseTime(end - start);
    }


    @Override
    public Void openPullTask(CharSequence serviceUrl, CharSequence table, int shardId, CharSequence method) throws AvroRemoteException {
        String methodStr = method == null ? null : method.toString();
        PullContext pullContext = KafkaPullContext.newInstance(table.toString(), shardId, methodStr);
        NodeBaseEntry nodeBaseEntry = NodeBaseEntryImpl.newInstance(serviceUrl.toString());
        SalverNode salverNode = taskDistributionService.getSalverNode(nodeBaseEntry);
        SalverRpcProtocol proxy = getProxyCache(nodeBaseEntry);
        proxy.openPullTask(serviceUrl, table, shardId, method);
        taskDistributionService.openPullTask(salverNode, pullContext);

        return null;
    }

    @Override
    public Void closePullTask(CharSequence serviceUrl, CharSequence table, int shardId) throws AvroRemoteException {
        PullContext pullContext = KafkaPullContext.newInstance(table.toString(), shardId);
        NodeBaseEntry nodeBaseEntry = NodeBaseEntryImpl.newInstance(serviceUrl.toString());
        SalverNode salverNode = taskDistributionService.getSalverNode(nodeBaseEntry);
        SalverRpcProtocol proxy = getProxyCache(nodeBaseEntry);
        proxy.closePullTask(serviceUrl, table, shardId);
        taskDistributionService.closePullTask(salverNode, pullContext);

        return null;
    }

    @Override
    public Void closePullTaskList(CharSequence serviceUrl, CharSequence table, List<Integer> shardIds) throws AvroRemoteException {
        List<PullContext> pullContextList = new ArrayList<>();
        for (int shardId : shardIds) {
            pullContextList.add(KafkaPullContext.newInstance(table.toString(), shardId));
        }

        NodeBaseEntry nodeBaseEntry = NodeBaseEntryImpl.newInstance(serviceUrl.toString());
        SalverNode salverNode = taskDistributionService.getSalverNode(nodeBaseEntry);
        SalverRpcProtocol proxy = getProxyCache(nodeBaseEntry);
        proxy.closePullTaskList(serviceUrl, table, shardIds);
        taskDistributionService.closePullTaskList(salverNode, pullContextList);

        return null;
    }

    @Override
    public CharSequence insertStat() throws AvroRemoteException {
        Set<SalverNode> salverNodes = taskDistributionService.getAllSalvers();

        List<Future<CharSequence>> futures = new ArrayList<>(salverNodes.size());

        salverNodes.forEach(salverNode -> {

            SalverRpcProtocol rpcProtocol = getProxyCache(salverNode.getNodeEntry());
            futures.add(ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(rpcProtocol::insertStat));

        });
        try {
            return mergerInsertStat(futures);
        } catch (JsonProcessingException e) {
            throw new AvroRemoteException(e);
        }
    }

    private CharSequence mergerInsertStat(List<Future<CharSequence>> futures) throws JsonProcessingException {
        ArrayNode arrayNode = JsonUtil.getPrettyObjectMapper().createArrayNode();
        for (Future<CharSequence> future : futures) {
            try {
                arrayNode.add(JsonUtil.getObjectMapper().readValue(future.get().toString(), JsonNode.class));
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }
        }
        return JsonUtil.getPrettyObjectMapper().writeValueAsString(arrayNode);
    }

    @Override
    public Void shutdown() {
        //todo
        Set<SalverNode> salverNodes = taskDistributionService.getAllSalvers();

        List<Future<Void>> futures = new ArrayList<>(salverNodes.size());

        salverNodes.forEach(salverNode -> {

            SalverRpcProtocol rpcProtocol = getProxyCache(salverNode.getNodeEntry());
            try {
                rpcProtocol.shutdown();
            } catch (AvroRemoteException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    /**
     * 同步获取缓存
     * @param nodeBaseEntry 标识
     * @return 代理
     */
    private SalverRpcProtocol getProxyCache(NodeBaseEntry nodeBaseEntry) {
        SalverRpcProtocol proxy = proxyCache.get(nodeBaseEntry);
        if (proxy == null) {
            cacheLock.lock();
            try {
                proxy = proxyCache.get(nodeBaseEntry);
                if (proxy == null) {
                    LOG.info("create rpc to {} server", nodeBaseEntry.getServiceUrl());
                    NettyTransceiver rpcClient = new NettyTransceiver(
                            new InetSocketAddress(nodeBaseEntry.getServiceHost(), nodeBaseEntry.getServicePort()),
                            NettyTransceiver.DEFAULT_CONNECTION_TIMEOUT_MILLIS * 3
                    );
                    proxy = SpecificRequestor.getClient(SalverRpcProtocol.class, rpcClient);
                    proxyCache.put(nodeBaseEntry, proxy);
                }
            } catch (IOException e) {
                LOG.error("create proxy error", e);
            } finally {
                cacheLock.unlock();
            }
        }
        return proxy;
    }

}
