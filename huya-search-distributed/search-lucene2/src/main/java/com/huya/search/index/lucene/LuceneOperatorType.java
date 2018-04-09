package com.huya.search.index.lucene;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/29.
 */
public enum LuceneOperatorType {
    RAM_SHARD,      //内存分片操作类型
    RAM,            //内存操作类型
    HDFS_ADN_RAM,   //hdfs与内存混合操作类型
    HDFS,           //hdfs操作类型
    LOCAL_AND_RAM,  //本地磁盘与内存混合操作类型
    LOCAL           //本地磁盘操作类型
    ;

    @Override
    public String toString() {
        return this.name();
    }
}
