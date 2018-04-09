package com.huya.search.index.opeation;

import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.SearchDataRow;

import java.util.Iterator;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public class InsertBulkContext implements ExecutorContext {

    private String table;

    private SearchData<SearchDataRow> searchData;

    public SearchData<SearchDataRow> getSearchData() {
        return searchData;
    }

    public InsertBulkContext setSearchData(SearchData<SearchDataRow> searchData) {
        this.searchData = searchData;
        return this;
    }

    @Override
    public String getTable() {
        return table;
    }

    public InsertBulkContext setTable(String table) {
        this.table = table;
        return this;
    }

    public Iterator<InsertContext> getIterator() {
        return new InsertContextIterator();
    }


    private class InsertContextIterator implements Iterator<InsertContext> {

        private Iterator<SearchDataRow> iterator;

        InsertContextIterator() {
            iterator = searchData.getCollection().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public InsertContext next() {
            return new InsertContext().setTable(table)
                    .setSearchDataRow(iterator.next());
        }
    }
}
