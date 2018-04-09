package com.huya.search.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.facing.subscriber.KafkaPullContext;
import com.huya.search.facing.subscriber.SubscriberService;
import com.huya.search.index.DisKryoSerializerInit;
import com.huya.search.index.Engine;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.opeation.DefaultShardsQueryContext;
import com.huya.search.index.opeation.DisQueryContext;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.index.opeation.ShardsQueryContext;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.avro.AvroRemoteException;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/15.
 */
public class SalverRpcProtocolImpl implements SalverRpcProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(SalverRpcProtocolImpl.class);

    private static final KryoPool KRYO_POOL = new KryoPool.Builder(() -> {
        Kryo kryo = new Kryo();
        DisKryoSerializerInit.run(kryo);
        return kryo;
    }).softReferences().build();

    public static SalverRpcProtocolImpl newInstance(SubscriberService subscriberService, ZooKeeperOperator zo) {
        return new SalverRpcProtocolImpl(subscriberService, zo);
    }

    protected SubscriberService subscriberService;

    protected Engine engine;

    protected ZooKeeperOperator zo;

    protected SalverRpcProtocolImpl(SubscriberService subscriberService, ZooKeeperOperator zo) {
        this.subscriberService = subscriberService;
        this.engine = subscriberService.getEngine();
        this.zo = zo;
    }

    @Override
    public Void refresh(RpcRefreshContext refreshContext) throws AvroRemoteException {
        //todo refresh
        return null;
    }

    @Override
    public RpcResult sql(ByteBuffer queryBytes, List<Integer> shardArray, int shardNum) throws AvroRemoteException {
        byte[] bytes = new byte[queryBytes.remaining()];
        queryBytes.get(bytes);
        Input input = new Input(bytes);

        LOG.info("========== start to borrow kryo ===========");
        Kryo kryo = KRYO_POOL.borrow();
        Object obj = kryo.readClassAndObject(input);
        DisQueryContext queryContext = (DisQueryContext)obj;
        KRYO_POOL.release(kryo);
        LOG.info("========== start to release kryo ===========");

        return sqlSelf(queryContext, shardArray, shardNum);
    }

    @Override
    public CharSequence syncPullTask() throws AvroRemoteException {
        return subscriberService.syncPullTask().toString();
    }

    RpcResult sqlSelf(DisQueryContext queryContext, List<Integer> shardArray, int shardNum) {
        List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList = new ArrayList<>(shardArray.size());
        LOG.info("========== start to query shard ===========");
        for (Integer shardId : shardArray) {
            ShardsQueryContext shardsQueryContext = DefaultShardsQueryContext.newInstance(shardId, shardNum, queryContext);
            queryResultFutureList.add(ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> engine.query(shardsQueryContext)));
        }
        LOG.info("========== end to query shard ===========");

        return queryContext.getShardMerger().merger(queryResultFutureList);
    }

    @Override
    public Void openPullTask(CharSequence serviceUrl, CharSequence table, int shardId, CharSequence method) throws AvroRemoteException {
        String methodStr = method == null ? null : method.toString();
        PullContext pullContext = KafkaPullContext.newInstance(table.toString(), shardId, methodStr);


        NodeBaseEntry currentEntry = zo.getNodeBaseEntry();

        if (Objects.equals(currentEntry.getServiceUrl(), serviceUrl.toString())) {
            openPullTaskSelf(pullContext);
        }
        return null;
    }

    void openPullTaskSelf(PullContext pullContext) throws AvroRemoteException {
        try {
            if (PullContext.PullMethod.FOLLOW == pullContext.getMethod()) {
                subscriberService.openPullTask(pullContext.getTable(), pullContext.getShardId());
            } else {
                subscriberService.openPullTaskFromEnd(pullContext.getTable(), pullContext.getShardId());
            }
        } catch (ShardsOperatorException e) {
            throw new AvroRemoteException(e);
        }
    }

    @Override
    public Void closePullTask(CharSequence serviceUrl, CharSequence table, int shardId) throws AvroRemoteException {
        PullContext pullContext = KafkaPullContext.newInstance(table.toString(), shardId);
        NodeBaseEntry currentEntry = zo.getNodeBaseEntry();
        if (Objects.equals(currentEntry.getServiceUrl(), serviceUrl.toString())) {
            closePullTaskSelf(pullContext);
        }
        return null;
    }

    @Override
    public Void closePullTaskList(CharSequence serviceUrl, CharSequence table, List<Integer> shardIds) throws AvroRemoteException {
        NodeBaseEntry currentEntry = zo.getNodeBaseEntry();
        if (Objects.equals(currentEntry.getServiceUrl(), serviceUrl.toString())) {
            closePullTaskListSelf(table.toString(), shardIds);
        }
        return null;
    }

    void closePullTaskListSelf(String table, List<Integer> shardIds) throws AvroRemoteException {
        try {
            subscriberService.closePullTask(table, shardIds);
        } catch (ShardsOperatorException e) {
            throw new AvroRemoteException(e);
        }
    }

    void closePullTaskSelf(PullContext pullContext) throws AvroRemoteException {
        try {
            subscriberService.closePullTask(pullContext.getTable(), pullContext.getShardId());
        } catch (ShardsOperatorException e) {
            throw new AvroRemoteException(e);
        }
    }

    @Override
    public CharSequence insertStat() throws AvroRemoteException {
        return insertStatSelf();
    }

    CharSequence insertStatSelf() {
        JsonNode jsonNode = subscriberService.getInsertStat();
        assert jsonNode instanceof ObjectNode;

        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put("salver", zo.getNodeBaseEntry().getServiceUrl());
        return objectNode.toString();
    }

    @Override
    public Void shutdown() throws AvroRemoteException {
        System.exit(0);
        return null;
//        throw new AvroRemoteException("salver no support shutdown");
    }
}
