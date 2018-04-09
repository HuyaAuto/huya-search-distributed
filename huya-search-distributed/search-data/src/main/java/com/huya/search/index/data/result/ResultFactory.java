package com.huya.search.index.data.result;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.SearchData;
import org.apache.lucene.index.IndexableField;

import java.util.UUID;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/6.
 */
public class ResultFactory {

    public static QueryResult EMPTY;

    public static QueryResult<? extends Iterable<IndexableField>> create(SearchData<? extends Iterable<IndexableField>> result, UUID uuid) {
        //todo 添加缓存部分代码
        return null;
    }

    public static QueryResult<? extends Iterable<IndexableField>> createJsonResult(QueryResult<? extends Iterable<IndexableField>> result) {
        return new JsonQueryResult<>(result);
    }

    public static QueryResult addRunTime(QueryResult queryResult, long l) {
        queryResult.setRunTime(l);
        return queryResult;
    }
}
