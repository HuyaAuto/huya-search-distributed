package com.huya.search.index.opeation;

import com.huya.search.index.dsl.parse.QueryComponent;
import com.huya.search.index.opeation.builder.DisDSLQueryBuilder;
import com.huya.search.index.opeation.builder.DisDSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public class DisQueryFactory extends QueryFactory implements QueryComponent, TranDisQueryContext {

    public static DisQueryFactory newInstance() {
        return new DisQueryFactory();
    }

    protected DisQueryFactory() {}

    @Override
    public DisQueryContext tranDis() {
        if (getSortedItems() == null) {
                DisDSLQueryBuilder disDSLQueryBuilder = new DisDSLQueryBuilder();
                disDSLQueryBuilder.setSelectItems(getSelectItems())
                        .setWhereCondition(getWhereCondition())
                        .setGroupByItems(getGroupByItems())
                        .setLimitExpr(getLimitExpr());
                return disDSLQueryBuilder.disBuilder();

        }
        else {
            DisDSLSortQueryBuilder disDSLSortQueryBuilder = new DisDSLSortQueryBuilder();
            disDSLSortQueryBuilder.setSortedItems(getSortedItems())
                    .setSelectItems(getSelectItems())
                    .setWhereCondition(getWhereCondition())
                    .setGroupByItems(getGroupByItems())
                    .setLimitExpr(getLimitExpr());
            return disDSLSortQueryBuilder.disBuilder();
        }
    }
}
