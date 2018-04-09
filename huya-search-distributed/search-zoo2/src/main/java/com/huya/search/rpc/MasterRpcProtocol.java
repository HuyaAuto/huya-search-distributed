package com.huya.search.rpc;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public interface MasterRpcProtocol extends SalverRpcProtocol {

    void createMeta(String metaJson);

    void removeMeta(String table);

    void updateMeta(String table, String content);

    void openMeta(String table);

    void closeMeta(String table);

    String meta(String table);

    String lastMeta(String table);

    String metas();

    String lastMetas();

    QueryDetailResult sql(String sql);

    String syncPullTask(String serverUrl) throws org.apache.avro.AvroRemoteException;

}
