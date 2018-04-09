package com.huya.search.index.data.merger;

import org.apache.lucene.index.IndexableField;

/**
 * 默认计算单元
 * 通过加入文档，自己进行聚合的计算
 * Created by zhangyiqun1@yy.com on 2018/3/3.
 */
public abstract class DefaultAggrFunUnit<T> implements AggrFunListener<T> {

    public abstract void add(IndexableField field);
}
