package com.huya.search.index.opeation.builder;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.SelectItems;
import com.huya.search.index.dsl.sorted.SortedItems;
import com.huya.search.index.dsl.where.WhereCondition;
import com.huya.search.index.opeation.DSLMixNewQueryContext;
import com.huya.search.index.opeation.DSLNewQueryContext;
import com.huya.search.index.opeation.DSLSortQueryContext;
import com.huya.search.index.opeation.QueryContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/8.
 */
public class DSLSortQueryBuilder extends DSLQueryBuilder {

    private SortedItems sortedItems;

    public SortedItems getSortedItems() {
        return sortedItems;
    }

    public DSLSortQueryBuilder setSortedItems(SortedItems sortedItems) {
        this.sortedItems = sortedItems;
        return this;
    }

    @Override
    public QueryContext builder() {
        if (sortedItems.onlySortByNewDoc()) {
            return DSLNewQueryContext.newInstance(this);
        }
        else if (sortedItems.mixSortByNewDoc()) {
            return DSLMixNewQueryContext.newInstance(this);
        }
        else {
            return DSLSortQueryContext.newInstance(this);
        }
    }

    public static class DSLSortQueryBuilderSerializer extends Serializer<DSLSortQueryBuilder> {

        @Override
        public void write(Kryo kryo, Output output, DSLSortQueryBuilder object) {
            kryo.writeObject(output, object.getSelectItems());
            kryo.writeObject(output, object.getWhereCondition());
            kryo.writeObjectOrNull(output, object.getGroupByItems(), GroupByItems.class);
            kryo.writeObject(output, object.getLimitExpr());
            kryo.writeObject(output, object.sortedItems);
        }

        @Override
        public DSLSortQueryBuilder read(Kryo kryo, Input input, Class<DSLSortQueryBuilder> type) {
            DSLSortQueryBuilder dslSortQueryBuilder = new DSLSortQueryBuilder();
            dslSortQueryBuilder.setSelectItems(kryo.readObject(input, SelectItems.class))
                    .setWhereCondition(kryo.readObject(input, WhereCondition.class))
                    .setGroupByItems(kryo.readObjectOrNull(input, GroupByItems.class))
                    .setLimitExpr(kryo.readObject(input, LimitExpr.class));

            return dslSortQueryBuilder.setSortedItems(kryo.readObject(input, SortedItems.class));
        }

    }
}
