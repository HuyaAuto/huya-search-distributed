package com.huya.search.index.data;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huya.search.index.data.util.DataConvert;
import com.huya.search.index.meta.JsonArrayAble;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.index.IndexableField;


import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public class SearchData<T extends Iterable<IndexableField>> implements JsonArrayAble {

    private List<T> collection = new ArrayList<>();

    public static <T extends Iterable<IndexableField>> SearchData<T> newInstance(List<T> list) {
        return new SearchData<>(list);
    }

    public SearchData(List<T> list) {
        this.collection = list;
    }

    public SearchData() {}

    public List<T> getCollection() {
        return collection;
    }

    public int size() {
        return collection.size();
    }

    public T get(int i) {
        return collection.get(i);
    }

    public void add(Collection<T> addCollection) {
        collection.addAll(addCollection);
    }

    @Override
    public ArrayNode toArray() {
        ArrayNode arrayNode = new ArrayNode(JsonUtil.getObjectMapper().getNodeFactory());
        collection.forEach((iterable) -> arrayNode.add(DataConvert.rowToObject(iterable)));
        return arrayNode;
    }

    public SearchData<? extends Iterable<IndexableField>> sub(int i, int limitSize) {
        collection = collection.subList(i, limitSize);
        return this;
    }
}
