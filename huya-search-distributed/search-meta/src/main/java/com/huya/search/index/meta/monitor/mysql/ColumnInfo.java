package com.huya.search.index.meta.monitor.mysql;

import com.huya.search.index.meta.MetaDefine;

/**
 * TODO rename TableColumn
 *
 * Created by zhangyiqun1@yy.com on 2017/10/20.
 */
public class ColumnInfo {

    private String table;

    private String partitionName;

    private String columnInfo;

    public String getTable() {
        return table;
    }

    public ColumnInfo setTable(String table) {
        this.table = table;
        return this;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public ColumnInfo setPartitionName(String partitionName) {
        this.partitionName = partitionName;
        return this;
    }

    public String getColumnInfo() {
        return columnInfo;
    }

    public ColumnInfo setColumnInfo(String columnInfo) {
        this.columnInfo = columnInfo;
        return this;
    }

    public static ColumnInfo newInstance(String table, MetaDefine metaDefine) {
        return new ColumnInfo()
                .setTable(table)
                .setPartitionName(metaDefine.getMetaDefineTimestampKey())
                .setColumnInfo(metaDefine.toObject().toString());
    }
}
