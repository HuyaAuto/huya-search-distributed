package com.huya.search;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.data.DataFactory;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.meta.MetaEnum;
import com.huya.search.index.opeation.RefreshContext;
import com.huya.search.rest.HttpOperator;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/9.
 */
public class HttpAccessMedium implements AccessMedium {

    public static HttpAccessMedium newInstance(String url) {
        return new HttpAccessMedium(url);
    }

    private HttpOperator httpOperator;

    private HttpAccessMedium(String url) {
        this.httpOperator = new HttpOperator(url);
    }


    @Override
    public void createMeta(String metaJson) {
        try {
            httpOperator.create(metaJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMeta(String table) {
        try {
            httpOperator.remove(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMeta(String content) {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        objectNode.put(MetaEnum.CONTENT, content);
        try {
            httpOperator.update(objectNode.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openMeta(String table) {
        try {
            httpOperator.open(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeMeta(String table) {
        try {
            httpOperator.close(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String viewMeta(String table) {
        try {
            return httpOperator.view(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(String table, SearchData<SearchDataRow> searchData) {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        objectNode.put(MetaEnum.TABLE, table);
        objectNode.set(MetaEnum.DATA, searchData.toArray());
        try {
            httpOperator.insert(objectNode.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh(RefreshContext refreshContext) {
        try {
            httpOperator.refresh(refreshContext.toObject().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> sql(String sql) {
        try {
            String json = httpOperator.sql(sql);
            ObjectNode objectNode = (ObjectNode) JsonUtil.getObjectMapper().readTree(json);
            return DataFactory.newQueryInstance(objectNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
