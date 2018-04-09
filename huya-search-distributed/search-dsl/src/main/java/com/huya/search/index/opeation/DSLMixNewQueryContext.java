package com.huya.search.index.opeation;

import com.huya.search.index.dsl.function.DslType;
import com.huya.search.index.opeation.builder.DSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public class DSLMixNewQueryContext extends DSLNewQueryContext {

    public static DSLMixNewQueryContext newInstance(DSLSortQueryBuilder dslSortQueryBuilder) {
        return new DSLMixNewQueryContext(dslSortQueryBuilder);
    }

    DSLMixNewQueryContext(DSLSortQueryBuilder dslSortQueryBuilder) {
        super(dslSortQueryBuilder);
    }

    @Override
    public DslType getDslType() {
        return DslType.MIX_NEW_QUERY_LIMIT;
    }
}
