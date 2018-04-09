package com.huya.search.index.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.JsonObjectAble;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.index.IndexableField;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/7.
 */
public class QueryResult<T extends Iterable<IndexableField>> extends SearchData<T> implements JsonObjectAble {

    public static final String RUN_TIME = "runtime";

    public static final String DATA     = "data";

    private long runTime;

    public QueryResult() {}

    public QueryResult(List<T> list) {
        super(list);
    }

    public QueryResult(SearchData<T> searchData) {
        super(searchData.getCollection());
    }

    public long getRunTime() {
        return runTime;
    }

    public QueryResult<T> setRunTime(long runTime) {
        this.runTime = runTime;
        return this;
    }

    @Override
    public ObjectNode toObject() {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        objectNode.put(RUN_TIME, runTime);
        objectNode.set(DATA, toArray());
        objectNode.put("code", 200);
        return objectNode;
    }

    @Override
    public String toString() {
        return toObject().toString();
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> sub(int i, int limitSize) {
        return new QueryResult<>(super.sub(i, limitSize));
    }
}
