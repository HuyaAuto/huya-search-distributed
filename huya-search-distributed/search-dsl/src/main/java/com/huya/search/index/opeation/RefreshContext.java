package com.huya.search.index.opeation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.JsonObjectAble;
import com.huya.search.index.meta.MetaEnum;
import com.huya.search.util.JsonUtil;

public class RefreshContext implements ExecutorContext, JsonObjectAble {

    private String table;

    private String timestamp;

    private boolean refreshAllTable     = false;

    private boolean refreshAllPartition = false;

    private boolean forced = false;

    public static RefreshContext buildFromJson(ObjectNode objectNode) {
        boolean refreshAllTable     = !objectNode.has(MetaEnum.TABLE);
        boolean refreshAllPartition = !objectNode.has(MetaEnum.TIMESTAMP);
        boolean forced              = objectNode.get("forced").asBoolean();
        if (refreshAllTable) {
            if (refreshAllPartition) return buildForAllTable(forced);
            else return buildForAllTable(objectNode.get(MetaEnum.TIMESTAMP).textValue(), forced);
        }
        else {
            if (refreshAllPartition) return build(objectNode.get(MetaEnum.TABLE).textValue(), forced);
            else return build(
                    objectNode.get(MetaEnum.TABLE).textValue(),
                    objectNode.get(MetaEnum.TIMESTAMP).textValue(),
                    forced
            );
        }
    }

    public static RefreshContext buildForAllTable(boolean forced) {
        return new RefreshContext()
                .setRefreshAllTable(true)
                .setRefreshAllPartition(true)
                .setForced(forced);
    }

    public static RefreshContext buildForAllTable(String timestamp, boolean forced) {
        return new RefreshContext()
                .setTimestamp(timestamp)
                .setRefreshAllTable(true)
                .setForced(forced);
    }


    public static RefreshContext build(String table, boolean forced) {
        return new RefreshContext()
                .setTable(table)
                .setRefreshAllPartition(true)
                .setForced(forced);
    }

    public static RefreshContext build(String table, String timestamp, boolean forced) {
        return new RefreshContext()
                .setTable(table)
                .setTimestamp(timestamp)
                .setForced(forced);
    }

    private RefreshContext() {}

    public RefreshContext setTable(String table) {
        this.table = table;
        return this;
    }

    public RefreshContext setRefreshAllPartition(boolean refreshAllPartition) {
        this.refreshAllPartition = refreshAllPartition;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public RefreshContext setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public String getTable() {
        return table;
    }

    public boolean isRefreshAllPartition() {
        return refreshAllPartition;
    }

    public boolean isRefreshAllTable() {
        return refreshAllTable;
    }

    public RefreshContext setRefreshAllTable(boolean refreshAllTable) {
        this.refreshAllTable = refreshAllTable;
        return this;
    }

    public boolean isForced() {
        return forced;
    }

    public RefreshContext setForced(boolean forced) {
        this.forced = forced;
        return this;
    }

    @Override
    public ObjectNode toObject() {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        if (table != null) objectNode.put(MetaEnum.TABLE, table);

        if (timestamp != null) objectNode.put(MetaEnum.TIMESTAMP, timestamp);

        objectNode.put("refreshAllTable", refreshAllTable);
        objectNode.put("refreshAllPartition", refreshAllPartition);
        objectNode.put("forced", forced);

        return objectNode;
    }
}
