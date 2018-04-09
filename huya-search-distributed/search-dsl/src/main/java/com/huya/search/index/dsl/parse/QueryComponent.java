package com.huya.search.index.dsl.parse;

import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.SelectItems;
import com.huya.search.index.dsl.sorted.SortedItems;
import com.huya.search.index.dsl.where.WhereCondition;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public interface QueryComponent {

    QueryComponent setTable(String table);

    QueryComponent setSelectItems(SelectItems selectItems);

    QueryComponent setWhereCondition(WhereCondition whereCondition);

    QueryComponent setGroupByItems(GroupByItems groupByItems);

    QueryComponent setSortedItems(SortedItems sortedItems);

    QueryComponent setLimitExpr(LimitExpr limitExpr);

}
