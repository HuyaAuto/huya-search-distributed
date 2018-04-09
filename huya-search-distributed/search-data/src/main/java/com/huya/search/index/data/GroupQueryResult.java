package com.huya.search.index.data;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/9.
 */
public class GroupQueryResult<T extends Iterable<IndexableField>> extends QueryResult<T> {

    private List<GroupSearchData<T>> searchDataList;

    private List<T> all;

    public GroupQueryResult(List<GroupSearchData<T>> searchDataList) {
        super();
        init(searchDataList);
    }

    private void init(List<GroupSearchData<T>> searchDataList) {
        this.searchDataList = searchDataList;
        this.all = new ArrayList<>();
        searchDataList.forEach(searchData -> all.addAll(searchData.getCollection()));
    }

    @Override
    public List<T> getCollection() {
        return all;
    }

    @Override
    public int size() {
        return all.size();
    }

    @Override
    public T get(int i) {
        return all.get(i);
    }

    public int groupSize() {
        return searchDataList.size();
    }

    public SearchData<T> getGroup(int i) {
        return searchDataList.get(i);
    }

    public GroupKey getGroupKey(int i) {
        return searchDataList.get(i).getGroupKey();
    }

    @Override
    public ArrayNode toArray() {
        ArrayNode arrayNode = new ArrayNode(JsonUtil.getObjectMapper().getNodeFactory());
        searchDataList.forEach(searchData -> arrayNode.add(searchData.toArray()));
        return arrayNode;
    }

}
