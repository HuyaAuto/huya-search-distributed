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
import com.huya.search.index.opeation.*;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public class DisDSLSortQueryBuilder extends DSLSortQueryBuilder implements DisQueryBuilder {

    @Override
    public DisQueryContext disBuilder() {
        if (getSelectItems().getAggrFunctionSelectItemList().size() > 0 ) {
            return DisDSLSortAggrQueryContext.newInstance(this);
        }
        else {
            if (getSortedItems().onlySortByNewDoc()) {
                return DisDSLNewQueryContext.newInstance(this);
            }
            else if (getSortedItems().mixSortByNewDoc()) {
                return DisDSLMixNewQueryContext.newInstance(this);
            }
            else {
                return DisDSLSortQueryContext.newInstance(this);
            }
        }
    }

    public static class DisDSLSortQueryBuilderSerializer extends Serializer<DisDSLSortQueryBuilder> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLSortQueryBuilder object) {
            kryo.writeObject(output, object.getSelectItems());
            kryo.writeObject(output, object.getWhereCondition());
            kryo.writeObjectOrNull(output, object.getGroupByItems(), GroupByItems.class);
            kryo.writeObject(output, object.getLimitExpr());
            kryo.writeObject(output, object.getSortedItems());
        }

        @Override
        public DisDSLSortQueryBuilder read(Kryo kryo, Input input, Class<DisDSLSortQueryBuilder> type) {
            DisDSLSortQueryBuilder disDSLSortQueryBuilder = new DisDSLSortQueryBuilder();
            disDSLSortQueryBuilder.setSelectItems(kryo.readObject(input, SelectItems.class))
                    .setWhereCondition(kryo.readObject(input, WhereCondition.class))
                    .setGroupByItems(kryo.readObjectOrNull(input, GroupByItems.class))
                    .setLimitExpr(kryo.readObject(input, LimitExpr.class));
            disDSLSortQueryBuilder.setSortedItems(kryo.readObject(input, SortedItems.class));
            return disDSLSortQueryBuilder;
        }

    }
}
