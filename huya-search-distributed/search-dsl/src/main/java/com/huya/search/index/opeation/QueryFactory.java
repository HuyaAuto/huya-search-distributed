package com.huya.search.index.opeation;

import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.parse.QueryComponent;
import com.huya.search.index.dsl.select.SelectItems;
import com.huya.search.index.dsl.sorted.SortedItems;
import com.huya.search.index.dsl.where.WhereCondition;
import com.huya.search.index.opeation.builder.DSLQueryBuilder;
import com.huya.search.index.opeation.builder.DSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public class QueryFactory implements QueryComponent, TranQueryContext {

    public static QueryFactory newInstance() {
        return new QueryFactory();
    }

    protected String table;

    protected SelectItems selectItems;

    protected WhereCondition whereCondition;

    protected GroupByItems groupByItems;

    protected SortedItems sortedItems;

    protected LimitExpr limitExpr;


    protected QueryFactory() {}

    @Override
    public QueryContext tran() {
        if (sortedItems == null) {
            return new DSLQueryBuilder()
                    .setSelectItems(selectItems)
                    .setWhereCondition(whereCondition)
                    .setGroupByItems(groupByItems)
                    .setLimitExpr(limitExpr)
                    .builder();
        }
        else {
            DSLSortQueryBuilder builder = new DSLSortQueryBuilder();
            builder.setSortedItems(sortedItems)
                    .setSelectItems(selectItems)
                    .setWhereCondition(whereCondition)
                    .setGroupByItems(groupByItems)
                    .setLimitExpr(limitExpr);
            return builder.builder();
        }
    }

    @Override
    public QueryFactory setTable(String table) {
        this.table = table;
        return this;
    }

    @Override
    public QueryFactory setSelectItems(SelectItems selectItems) {
        this.selectItems = selectItems;
        return this;
    }

    @Override
    public QueryFactory setWhereCondition(WhereCondition whereCondition) {
        this.whereCondition = whereCondition;
        return this;
    }

    @Override
    public QueryFactory setGroupByItems(GroupByItems groupByItems) {
        this.groupByItems = groupByItems;
        return this;
    }

    @Override
    public QueryFactory setSortedItems(SortedItems sortedItems) {
        this.sortedItems = sortedItems;
        return this;
    }

    @Override
    public QueryFactory setLimitExpr(LimitExpr limitExpr) {
        this.limitExpr = limitExpr;
        return this;
    }

    public String getTable() {
        return table;
    }

    public SelectItems getSelectItems() {
        return selectItems;
    }

    public WhereCondition getWhereCondition() {
        return whereCondition;
    }

    public GroupByItems getGroupByItems() {
        return groupByItems;
    }

    public SortedItems getSortedItems() {
        return sortedItems;
    }

    public LimitExpr getLimitExpr() {
        return limitExpr;
    }
}
