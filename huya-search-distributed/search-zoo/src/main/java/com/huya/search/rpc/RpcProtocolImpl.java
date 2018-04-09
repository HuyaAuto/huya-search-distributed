package com.huya.search.rpc;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.index.Engine;
import com.huya.search.index.IndexEngine;
import com.huya.search.index.block.DisDataBlockManager;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.index.opeation.*;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.meta.MasterMetaService;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.salvers.SalverNode;
import com.huya.search.node.salvers.Salvers;
import com.huya.search.tasks.MasterTasksService;
import com.huya.search.util.JsonUtil;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.lucene.index.IndexableField;
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
 * Created by zhangyiqun1@yy.com on 2017/11/14.
 */
public class RpcProtocolImpl implements RpcProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(RpcProtocolImpl.class);

    private Map<NodeBaseEntry, RpcProtocol> proxyCache = new HashMap<>();

    private ReentrantLock cacheLock = new ReentrantLock();

    public static RpcProtocolImpl newInstance(RpcService rpcService, MasterMetaService masterMetaService, MasterTasksService masterTasksService,
                                              SubscriberService subscriberService) {
        return new RpcProtocolImpl(rpcService, masterMetaService, masterTasksService, subscriberService);
    }

    private RpcService rpcService;

    private MasterMetaService masterMetaService;

    private MasterTasksService masterTasksService;

    private SubscriberService subscriberService;

    private Engine engine = ModulesBuilder.getInstance().createInjector().getInstance(IndexEngine.class);

    private RpcProtocolImpl(RpcService rpcService, MasterMetaService masterMetaService, MasterTasksService masterTasksService, SubscriberService subscriberService) {
        this.rpcService = rpcService;
        this.masterMetaService = masterMetaService;
        this.masterTasksService = masterTasksService;
        this.subscriberService = subscriberService;
    }

    //todo 元数据增删改查的具体实现

    @Override
    public Void createMeta(CharSequence metaJson) throws AvroRemoteException {
        if (rpcService.isMaster()) {
            TimelineMetaDefine metaDefine = JsonMetaUtil.createTimelineMetaDefineFromStr(metaJson.toString());
            masterMetaService.createMeta(metaDefine);
            return null;
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support createMeta");   
        }
    }

    @Override
    public Void removeMeta(CharSequence table) throws AvroRemoteException {
        if (rpcService.isMaster()) {
            masterMetaService.removeMeta(table.toString());
            return null;
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support removeMeta");
        }
    }

    @Override
    public Void updateMeta(CharSequence table, CharSequence content) throws AvroRemoteException {
        if (rpcService.isMaster()) {
            masterMetaService.updateMeta(table.toString(), JsonMetaUtil.createMetaDefineFromStr(content.toString()));
            return null;
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support updateMeta");
        }
    }

    @Override
    public Void openMeta(CharSequence table) throws AvroRemoteException {
        if (rpcService.isMaster()) {

            return null;
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support openMeta");
        }
    }

    @Override
    public Void closeMeta(CharSequence table) throws AvroRemoteException {
        if (rpcService.isMaster()) {

            return null;
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support closeMeta");
        }
    }

    @Override
    public CharSequence meta(CharSequence table) throws AvroRemoteException {
        if (rpcService.isMaster()) {
            TimelineMetaDefine timelineMetaDefine = masterMetaService.meta(table.toString());
            return timelineMetaDefine.toObject().toString();
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support meta");
        }
    }

    @Override
    public CharSequence lastMeta(CharSequence table) throws AvroRemoteException {
        if (rpcService.isMaster()) {
            TimelineMetaDefine timelineMetaDefine = masterMetaService.meta(table.toString());
            return timelineMetaDefine.getLast().toObject().toString();
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support lastMeta");
        }
    }

    @Override
    public CharSequence metas() throws AvroRemoteException {
        if (rpcService.isMaster()) {
            Iterator<TimelineMetaDefine> iterator = masterMetaService.metas();
            ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
            while (iterator.hasNext()) {
                arrayNode.add(iterator.next().toObject());
            }
            return arrayNode.toString();
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support metas");
        }    }

    @Override
    public CharSequence lastMetas() throws AvroRemoteException {
        if (rpcService.isMaster()) {
            Iterator<TimelineMetaDefine> iterator = masterMetaService.metas();
            ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
            while (iterator.hasNext()) {
                arrayNode.add(iterator.next().getLast().toObject());
            }
            return arrayNode.toString();
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support lastMeta");
        }
    }

    @Override
    public Void refresh(RpcRefreshContext refreshContext) throws AvroRemoteException {
        if (rpcService.isMaster()) {

            return null;
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support refresh");
        }
    }

    @Override
    public RpcResult sql(ByteBuffer queryBytes, List<Integer> shardArray, int shardNum) throws AvroRemoteException {
        byte[] bytes = new byte[queryBytes.remaining()];
        queryBytes.get(bytes);
        Input input = new Input(bytes);

        Object obj = KryoSingleton.kryo.readClassAndObject(input);
        DisQueryContext queryContext = (DisQueryContext)obj;

        return sqlSelf(queryContext, shardArray, shardNum);
    }

    private RpcResult sqlSelf(DisQueryContext queryContext, List<Integer> shardArray, int shardNum) {
        List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList = new ArrayList<>(shardArray.size());

        for (Integer shardId : shardArray) {
            ShardsQueryContext shardsQueryContext = DefaultShardsQueryContext.newInstance(shardId, shardNum, queryContext);
            queryResultFutureList.add(ThreadPoolSingleton.getInstance().getExecutorService().submit(() -> engine.query(shardsQueryContext)));
        }

        return queryContext.getShardMerger().merger(queryResultFutureList);
    }

    @Override
    public RpcResult sqlLine(CharSequence sql) throws AvroRemoteException {
        NodeBaseEntry currentEntry = rpcService.getCurrentNodeBaseEntry();

        if (rpcService.isMaster()) {
            DisQueryContext disQueryContext = DisOperationFactory.getQueryContext(sql.toString());

            Output output = new Output(new ByteArrayOutputStream());
            KryoSingleton.kryo.writeClassAndObject(output, disQueryContext);
            ByteBuffer byteBuffer = ByteBuffer.wrap(output.getBuffer());

            Salvers salvers = masterTasksService.getDistributedEngine().getAllSalvers();

            String table = disQueryContext.getTable();

            Set<SalverNode> salverNodes = salvers.getSalvers(table);

            int shardNum = salvers.getShardNum(table);

            List<Future<RpcResult>> futures = new ArrayList<>(salverNodes.size());

            salverNodes.forEach(salverNode -> {

                //todo 暂时使用机器所包含的分片数量，未来将改为整个表所包含的分片数量，降低单个分片的计算量
                List<Integer> integers = salverNode.getShardIdList(table);
                LOG.info("current {}  salverNode {}", currentEntry.getServiceUrl(), salverNode.getNodeEntry().getServiceUrl());
                if (Objects.equals(currentEntry.getServiceUrl(), salverNode.getNodeEntry().getServiceUrl())) {
                    futures.add(ThreadPoolSingleton.getInstance().getExecutorService().submit(() -> sqlSelf(disQueryContext, integers, shardNum)));
                }
                else {
                    RpcProtocol rpcProtocol = getProxyCache(currentEntry, salverNode.getNodeEntry());
                    futures.add(ThreadPoolSingleton.getInstance().getExecutorService().submit(() -> rpcProtocol.sql(byteBuffer, integers, shardNum)));
                }
            });

            return disQueryContext.getNodeMerger().merger(futures);
        }
        else {
            throw new AvroRemoteException("salver rpc server is not support independent sqlLine");
        }
    }

    @Override
    public boolean openPullTask(CharSequence serviceUrl, CharSequence table, int shardId, CharSequence method) throws AvroRemoteException {
        NodeBaseEntry currentEntry = rpcService.getCurrentNodeBaseEntry();

        if (Objects.equals(currentEntry.getServiceUrl(), serviceUrl.toString())) {
            if (PullContext.PullMethod.FOLLOW.toString().contentEquals(method)) {
                subscriberService.openPullTask(table.toString(), shardId);
            }
            else {
                subscriberService.openPullTaskFromEnd(table.toString(), shardId);
            }
            return true;
        }
        else {
            NodeBaseEntry nodeBaseEntry = DisDataBlockManager.NodeBaseEntryImpl.newInstance(serviceUrl.toString());
            RpcProtocol proxy = getProxyCache(currentEntry, nodeBaseEntry);
            return proxy.openPullTask(serviceUrl, table, shardId, method);
        }
    }

    @Override
    public boolean closePullTask(CharSequence serviceUrl, CharSequence table, int shardId) throws AvroRemoteException {
        NodeBaseEntry currentEntry = rpcService.getCurrentNodeBaseEntry();

        if (Objects.equals(currentEntry.getServiceUrl(), serviceUrl.toString())) {
            subscriberService.closePullTask(table.toString(), shardId);
            return true;
        }
        else {
            NodeBaseEntry nodeBaseEntry = DisDataBlockManager.NodeBaseEntryImpl.newInstance(serviceUrl.toString());
            RpcProtocol proxy = getProxyCache(currentEntry, nodeBaseEntry);
            return proxy.closePullTask(serviceUrl, table, shardId);
        }
    }

    @Override
    public CharSequence insertStat() throws AvroRemoteException {
        NodeBaseEntry currentEntry = rpcService.getCurrentNodeBaseEntry();

        if (rpcService.isMaster()) {
            Salvers salvers = masterTasksService.getDistributedEngine().getAllSalvers();
            Set<SalverNode> salverNodes = salvers.getSalvers();

            List<Future<CharSequence>> futures = new ArrayList<>(salverNodes.size());

            salverNodes.forEach(salverNode -> {
                if (Objects.equals(currentEntry.getServiceUrl(), salverNode.getNodeEntry().getServiceUrl())) {
                    futures.add(ThreadPoolSingleton.getInstance().getExecutorService().submit(this::insertStatSelf));
                }
                else {
                    RpcProtocol rpcProtocol = getProxyCache(currentEntry, salverNode.getNodeEntry());
                    futures.add(ThreadPoolSingleton.getInstance().getExecutorService().submit(rpcProtocol::insertStat));
                }
            });
            try {
                return mergerInsertStat(futures);
            } catch (JsonProcessingException e) {
                throw new AvroRemoteException(e);
            }
        }
        else {
            return insertStatSelf();
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
    public Void shutdown() throws AvroRemoteException {
        masterTasksService.tasksShutdown();
        return null;
    }

    private CharSequence insertStatSelf() {
        JsonNode jsonNode = subscriberService.getInsertStat();
        assert jsonNode instanceof ObjectNode;

        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put("salver", rpcService.getCurrentNodeBaseEntry().getServiceUrl());
        return objectNode.toString();
    }

    /**
     * 同步获取缓存
     * @param nodeBaseEntry 标识
     * @return 代理
     */
    private RpcProtocol getProxyCache(NodeBaseEntry currentEntry ,NodeBaseEntry nodeBaseEntry) {
        RpcProtocol proxy = proxyCache.get(nodeBaseEntry);
        if (proxy == null) {
            cacheLock.lock();
            try {
                proxy = proxyCache.get(nodeBaseEntry);
                if (proxy == null) {
                    LOG.info("create rpc {} client to {} server", currentEntry.getServiceUrl(), nodeBaseEntry.getServiceUrl());
                    NettyTransceiver rpcClient = new NettyTransceiver(
                            new InetSocketAddress(nodeBaseEntry.getServiceHost(), nodeBaseEntry.getServicePort()),
                            NettyTransceiver.DEFAULT_CONNECTION_TIMEOUT_MILLIS * 3
                    );
                    proxy = SpecificRequestor.getClient(RpcProtocol.class, rpcClient);
                    proxyCache.put(nodeBaseEntry, proxy);
                }
            } catch (IOException e) {
                Log.error("create proxy error", e);
            } finally {
                cacheLock.unlock();
            }
        }
        return proxy;
    }

}
