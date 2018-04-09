package com.huya.search.index.opeation.builder;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.SelectItems;
import com.huya.search.index.dsl.where.WhereCondition;
import com.huya.search.index.opeation.DSLDefaultQueryContext;
import com.huya.search.index.opeation.QueryContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public class DSLQueryBuilder implements QueryBuilder {

    private SelectItems selectItems;

    private WhereCondition whereCondition;

    private GroupByItems groupByItems;

    private LimitExpr limitExpr;

    public DSLQueryBuilder setSelectItems(SelectItems selectItems) {
        this.selectItems = selectItems;
        return this;
    }

    public DSLQueryBuilder setWhereCondition(WhereCondition whereCondition) {
        this.whereCondition = whereCondition;
        return this;
    }

    public DSLQueryBuilder setGroupByItems(GroupByItems groupByItems) {
        this.groupByItems = groupByItems;
        return this;
    }

    public DSLQueryBuilder setLimitExpr(LimitExpr limitExpr) {
        this.limitExpr = limitExpr;
        return this;
    }

    public SelectItems getSelectItems() {
        return selectItems;
    }

    public GroupByItems getGroupByItems() {
        return groupByItems;
    }

    public LimitExpr getLimitExpr() {
        return limitExpr;
    }

    public WhereCondition getWhereCondition() {
        return whereCondition;
    }

    @Override
    public QueryContext builder() {
        return DSLDefaultQueryContext.newInstance(this);
    }

    public static class DSLQueryBuilderSerializer extends Serializer<DSLQueryBuilder> {

        @Override
        public void write(Kryo kryo, Output output, DSLQueryBuilder object) {
            kryo.writeObject(output, object.selectItems);
            kryo.writeObject(output, object.whereCondition);
            kryo.writeObjectOrNull(output, object.groupByItems, GroupByItems.class);
            kryo.writeObject(output, object.limitExpr);
        }

        @Override
        public DSLQueryBuilder read(Kryo kryo, Input input, Class<DSLQueryBuilder> type) {
            return new DSLQueryBuilder()
                    .setSelectItems(kryo.readObject(input, SelectItems.class))
                    .setWhereCondition(kryo.readObject(input, WhereCondition.class))
                    .setGroupByItems(kryo.readObjectOrNull(input, GroupByItems.class))
                    .setLimitExpr(kryo.readObject(input, LimitExpr.class));
        }
    }

}
