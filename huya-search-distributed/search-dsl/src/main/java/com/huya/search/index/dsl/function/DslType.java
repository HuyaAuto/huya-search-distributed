package com.huya.search.index.dsl.function;

/**
 * 查询类型
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public enum DslType {

    DEFAULT_QUERY_LIMIT,    //默认查询，按照条件提取有限的明细数据，不涉及排序，最新等规则
    NEW_QUERY_LIMIT,        //最新查询，按照条件提取有限的、最新写入的明细数据
    MIX_NEW_QUERY_LIMIT,    //最新排序查询，按照条件提取最新的文档，再根据字段排序规则进行排序
    ORDER_QUERY_LIMIT,      //排序查询，按照条件与排序规则提取有限的明细数据
    AGGR_QUERY,             //聚合查询，按照条件计算聚合结果

}
