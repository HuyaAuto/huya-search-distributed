package com.huya.search.facing.convert;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/23.
 */
public interface DataConvert<T, P, R> {

    R convert(int id, long offset, T t, P p) throws IgnorableConvertException;

    /**
     * 设置表名，用于跟踪累积消费数
     *
     * @param table
     */
    void setTable(String table);

    /**
     * 分片
     *
     * @param shardId
     */
    void setShardId(int shardId);
}
