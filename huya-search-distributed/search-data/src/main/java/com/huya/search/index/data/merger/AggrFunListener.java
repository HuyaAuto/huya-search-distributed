package com.huya.search.index.data.merger;

import com.huya.search.index.data.function.AggrFun;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public interface AggrFunListener<T>  {

    AggrFun getType();

    IndexableField result();

    T getValue();

    void clear();

}
