package com.huya.search.index.data.merger;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.data.function.AggrFun;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public abstract class AggrFunSetBuilder {

    private Map<String, Set<DefaultAggrFunUnit>> buildMap = new HashMap<>();

    /**
     * 添加数据聚合监听
     * @param listenerField 监听的字段名称
     * @param resultField 返回结果的字段名称
     * @param aggrFun 聚合函数
     * @param resultIndexFieldType 聚合结果的字段类型
     */
    public void add(String listenerField, String resultField, AggrFun aggrFun, IndexFieldType resultIndexFieldType) {
        Set<DefaultAggrFunUnit> set = buildMap.computeIfAbsent(listenerField, k -> new HashSet<>());
        switch (resultIndexFieldType) {
            case Long:
            case Date:
            case Integer: set.add(getLongUnit(resultField, aggrFun)); break;
            case Float:
            case Double: set.add(getDoubleUnit(resultField, aggrFun)); break;
            case String:
            case Text: set.add(getStringUnit(resultField, aggrFun)); break;
        }
    }

    protected abstract DefaultAggrFunUnit getStringUnit(String resultField, AggrFun aggrFun);

    protected abstract DefaultAggrFunUnit getDoubleUnit(String resultField, AggrFun aggrFun);

    protected abstract DefaultAggrFunUnit getLongUnit(String resultField, AggrFun aggrFun);

    public DefaultAggrFunUnitSet build() {
        return DefaultAggrFunUnitSet.newInstance(buildMap);
    }

//    private Map<String, Set<DefaultAggrFunUnit>> buildMapCopy() {
//        Map<String, Set<DefaultAggrFunUnit>> copyBuildMap = new HashMap<>();
//        buildMap.forEach((key, value) -> {
//            copyBuildMap.put(key, copySetAggrFunListener(value));
//        });
//        return copyBuildMap;
//    }
//
//    private Set<DefaultAggrFunUnit> copySetAggrFunListener(Set<DefaultAggrFunUnit> set) {
//        Set<DefaultAggrFunUnit> copySet = new HashSet<>();
//        set.forEach(aggrFunListener -> copySet.add(aggrFunListener.clone()));
//        return set;
//    }

}
