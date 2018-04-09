package com.huya.search.index.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.data.result.AvroQueryResult;
import com.huya.search.index.data.result.AvroQueryResultRow;
import com.huya.search.index.data.util.DataConvert;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/13.
 */
public class DataFactory {

//    public static SearchData<SearchDataRow> newInsertInstance(ArrayNode arrayNode) {
//        List<SearchDataRow> list = new ArrayList<>();
//        arrayNode.forEach((jsonNode) -> list.add(SearchDataRow.buildFromJson((ObjectNode) jsonNode)));
//        return SearchData.newInstance(list);
//    }

    public static QueryResult<? extends Iterable<IndexableField>> newQueryInstance(ObjectNode objectNode) {
        long runTime = objectNode.get(QueryResult.RUN_TIME).longValue();
        JsonNode array = objectNode.get(QueryResult.DATA);
        List<Iterable<IndexableField>> list = new ArrayList<>();
        array.forEach((jsonNode) -> list.add(DataConvert.objectToRow((ObjectNode) jsonNode)));
        QueryResult<Iterable<IndexableField>> queryResult = new QueryResult<>(list);
        queryResult.setRunTime(runTime);
        return queryResult;
    }

    public static AvroQueryResult newInstance(List<AvroQueryResultRow> collection) {
        return new AvroQueryResult(collection);
    }

}
