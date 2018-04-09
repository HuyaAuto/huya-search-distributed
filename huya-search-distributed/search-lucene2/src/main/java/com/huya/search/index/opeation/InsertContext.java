package com.huya.search.index.opeation;

import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import org.apache.lucene.index.IndexableField;

import java.util.Iterator;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public class InsertContext implements ExecutorContext, ShardContext {

    protected InsertContext() {}

    private String table;

    private SearchDataRow searchDataRow;

    @Override
    public String getTable() {
        return table;
    }

    public InsertContext setTable(String table) {
        this.table = table;
        return this;
    }

    public SearchDataRow getSearchDataRow() {
        return searchDataRow;
    }

    public InsertContext setSearchDataRow(SearchDataRow searchDataRow) {
        this.searchDataRow = searchDataRow;
        return this;
    }

    public long unixTime() {
        return searchDataRow.getUnixTime();
    }

    public Iterator<SearchDataItem> getIterator() {
        return searchDataRow.getItems().iterator();
    }

    public int id() {
        return searchDataRow.getId();
    }

    public long offset() {
        return searchDataRow.getOffset();
    }

    @Override
    public long getUnixTime() {
        return unixTime();
    }

    @Override
    public int getShardId() {
        return id();
    }
}
