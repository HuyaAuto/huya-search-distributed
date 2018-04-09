package com.huya.search.index;

import com.esotericsoftware.kryo.Kryo;
import com.huya.search.index.opeation.*;
import com.huya.search.index.opeation.builder.DisDSLQueryBuilder;
import com.huya.search.index.opeation.builder.DisDSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public class DisKryoSerializerInit extends KryoSerializerInit {

    public static void run(){
        KryoSerializerInit.run();
        Kryo kryo = KryoSingleton.kryo;
        kryo.register(DisDSLDefaultQueryContext.class, new DisDSLDefaultQueryContext.DisDSLDefaultQueryContextSerializer());
        kryo.register(DisDSLNewQueryContext.class, new DisDSLNewQueryContext.DisDSLNewQueryContextSerializer());
        kryo.register(DisDSLMixNewQueryContext.class, new DisDSLMixNewQueryContext.DisDSLMixNewQueryContextSerializer());
        kryo.register(DisDSLDefaultAggrQueryContext.class, new DisDSLDefaultAggrQueryContext.DisDSLDefaultAggrQueryContextSerializer());
        kryo.register(DisDSLSortAggrQueryContext.class, new DisDSLSortAggrQueryContext.DisDSLSortAggrQueryContextSerializer());
        kryo.register(DisDSLSortQueryContext.class, new DisDSLSortQueryContext.DisDSLSortQueryContextSerializer());
        kryo.register(DisDSLQueryBuilder.class, new DisDSLQueryBuilder.DisDSLQueryBuilderSerializer());
        kryo.register(DisDSLSortQueryBuilder.class, new DisDSLSortQueryBuilder.DisDSLSortQueryBuilderSerializer());
    }
}
