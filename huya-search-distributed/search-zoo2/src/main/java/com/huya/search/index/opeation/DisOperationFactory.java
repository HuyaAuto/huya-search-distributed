package com.huya.search.index.opeation;

import com.huya.search.index.dsl.parse.ComponentFill;
import com.huya.search.index.dsl.parse.SearchSQLComponentFill;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public class DisOperationFactory {

    public static DisIndexContext getIndexContext(String table) {
        return new DisIndexContext().setTable(table);
    }

    public static DisShardIndexContext getShardIndexContext(String table, int shardId) {
        DisShardIndexContext disShardIndexContext = new DisShardIndexContext();
        disShardIndexContext.setShardId(shardId).setTable(table);
        return disShardIndexContext;
    }

    public static DisQueryContext getQueryContext(String dsl) {
        ComponentFill componentFill = SearchSQLComponentFill.newInstance(dsl);
        DisQueryFactory disQueryFactory = DisQueryFactory.newInstance();
        componentFill.fillComponent(disQueryFactory);
        return disQueryFactory.tranDis();
    }
}
