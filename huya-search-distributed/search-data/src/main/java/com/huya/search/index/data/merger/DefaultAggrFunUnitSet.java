package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import org.apache.lucene.index.IndexableField;

import java.util.*;

/**
 * 聚合函数集合
 * 用于输出一系列文档，在这些聚合函数的计算逻辑下的聚合结果
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class DefaultAggrFunUnitSet implements AggrFunUnitSet {

    public static DefaultAggrFunUnitSet newInstance(Map<String, Set<DefaultAggrFunUnit>> map) {
        return new DefaultAggrFunUnitSet(map);
    }

    private Map<String, Set<DefaultAggrFunUnit>> map;

    private List<IndexableField> groupByList;

    private DefaultAggrFunUnitSet(Map<String, Set<DefaultAggrFunUnit>> map) {
        this.map = map;
    }

    /**
     * 添加一个域用于计算聚合结果
     * @param indexableField
     */
    public void add(IndexableField indexableField) {
        String field = indexableField.name();
        Set<DefaultAggrFunUnit> set = map.get(field);
        if (set != null) {
            set.forEach(aggrFunListener -> aggrFunListener.add(indexableField));
        }
    }

    /**
     * 添加这一组聚合结果所对应的分组域值
     * @param indexableFields
     */
    public void addGroupBy(List<IndexableField> indexableFields) {
        groupByList = indexableFields;
    }

    /**
     * 返回聚合结果
     * @return 聚合结果
     */
    public QueryResult<Iterable<IndexableField>> result() {
        List<IndexableField> temp = new ArrayList<>();
        map.values().forEach(aggrFunListeners -> aggrFunListeners.forEach(aggrFunListener -> {
            temp.add(aggrFunListener.result());
        }));

        if (groupByList != null) {
            temp.addAll(groupByList);
        }

        return new QueryResult<>(Collections.singletonList(temp));
    }

    /**
     * 返回聚合域名称集合
     * @return
     */
    public Set<String> aggrFields() {
        return map.keySet();
    }

    /**
     * 清空聚合结果
     * 此对象可以重复使用，清空后加入需要聚合的域值和分组又可重新计算
     */
    public void clear() {
        if (groupByList != null) {
            groupByList.clear();
            groupByList = null;
        }
        map.values().forEach(aggrFunListeners -> aggrFunListeners.forEach(AggrFunListener::clear));
    }
}
