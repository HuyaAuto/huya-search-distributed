package com.huya.search.rpc;

import org.apache.avro.AvroRemoteException;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/24.
 */
public class TestRpcProtocolImpl implements RpcProtocol {
    @Override
    public Void createMeta(CharSequence metaJson) throws AvroRemoteException {
        return null;
    }

    @Override
    public Void removeMeta(CharSequence table) throws AvroRemoteException {
        return null;
    }

    @Override
    public Void updateMeta(CharSequence table, CharSequence content) throws AvroRemoteException {
        return null;
    }

    @Override
    public Void openMeta(CharSequence table) throws AvroRemoteException {
        return null;
    }

    @Override
    public Void closeMeta(CharSequence table) throws AvroRemoteException {
        return null;
    }

    @Override
    public CharSequence meta(CharSequence table) throws AvroRemoteException {
        return null;
    }

    @Override
    public CharSequence lastMeta(CharSequence table) throws AvroRemoteException {
        return null;
    }

    @Override
    public CharSequence metas() throws AvroRemoteException {
        return null;
    }

    @Override
    public CharSequence lastMetas() throws AvroRemoteException {
        return null;
    }

    @Override
    public Void refresh(RpcRefreshContext refreshContext) throws AvroRemoteException {
        return null;
    }

    @Override
    public RpcResult sql(ByteBuffer queryBytes, List<Integer> shardArray, int shardNum) throws AvroRemoteException {
        return null;
    }

    @Override
    public RpcResult sqlLine(CharSequence sql) throws AvroRemoteException {
        return null;
    }

    @Override
    public boolean openPullTask(CharSequence serviceUrl, CharSequence table, int shardId, CharSequence method) throws AvroRemoteException {
        return false;
    }


    @Override
    public boolean closePullTask(CharSequence serviceUrl, CharSequence table, int shardId) throws AvroRemoteException {
        return false;
    }

    @Override
    public CharSequence insertStat() throws AvroRemoteException {
        return null;
    }

    @Override
    public Void shutdown() throws AvroRemoteException {
        return null;
    }
}
