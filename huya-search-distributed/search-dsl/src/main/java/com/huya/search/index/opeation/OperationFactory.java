package com.huya.search.index.opeation;

import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.dsl.parse.ComponentFill;
import com.huya.search.index.dsl.parse.SearchSQLComponentFill;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public class OperationFactory {

    public static InsertContext getInsertContext(String table, int id, long offset, String timestamp, List<SearchDataItem> items) {
        return getInsertContext(table, new SearchDataRow().setId(id).setOffset(offset).setUnixTime(timestamp).setItems(items));
    }

    public static InsertContext getInsertContext(String table, SearchDataRow searchDataRow) {
        InsertContext context = new InsertContext();
        return context.setTable(table)
                .setSearchDataRow(searchDataRow);
    }

    public static InsertBulkContext getInsertContext(String table, SearchData<SearchDataRow> searchData) {
        InsertBulkContext context = new InsertBulkContext();
        return context.setTable(table)
                .setSearchData(searchData);
    }

    public static QueryContext getQueryContext(String dsl) {
        ComponentFill componentFill = SearchSQLComponentFill.newInstance(dsl);
        QueryFactory queryFactory = QueryFactory.newInstance();
        componentFill.fillComponent(queryFactory);
        return queryFactory.tran();
    }

    public static RefreshContext getRefreshContext(String table) {
        return RefreshContext.build(table, false);
    }

    public static RefreshContext getRefreshContext(String table, String timestamp) {
        return RefreshContext.build(table, timestamp, false);
    }

    public static RestoreContext getRestoreContext(String table, int shardId) {
        return new RestoreContext().setTable(table).setShardId(shardId);
    }

    public static ExistContext getExistContext(String table, int shardId, long offset, long unixTime) {
        return new ExistContext().setTable(table).setShardId(shardId).setOffset(offset).setUnixTime(unixTime);
    }

    public static CloseContext getCloseContext(String table, int shardId) {
        return CloseContext.newInstance(table, shardId);
    }

    public static PartitionRestoreContext getPartitionRestore(String table, int shardId, long unixTime) {
        PartitionRestoreContext partitionRestoreContext = new PartitionRestoreContext();
        partitionRestoreContext.setTable(table).setShardId(shardId);
        return partitionRestoreContext.setUnixTime(unixTime);
    }

    public static OpenContext getOpenShardsContext(String table, int shardId) {
        return OpenContext.newInstance(table, shardId);
    }

    public static CloseContext getCloseShardsContext(String table, int shardId) {
        return CloseContext.newInstance(table, shardId);
    }
}
