package com.huya.search.index.opeation.builder;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.SelectItems;
import com.huya.search.index.dsl.where.WhereCondition;
import com.huya.search.index.opeation.DisDSLDefaultAggrQueryContext;
import com.huya.search.index.opeation.DisDSLDefaultQueryContext;
import com.huya.search.index.opeation.DisQueryContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public class DisDSLQueryBuilder extends DSLQueryBuilder implements DisQueryBuilder {

    @Override
    public DisQueryContext disBuilder() {
        if (getSelectItems().getAggrFunctionSelectItemList().size() > 0 ) {
            return DisDSLDefaultAggrQueryContext.newInstance(this);
        }
        else {
            return DisDSLDefaultQueryContext.newInstance(this);
        }
    }

    public static class DisDSLQueryBuilderSerializer extends Serializer<DisDSLQueryBuilder> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLQueryBuilder object) {
            kryo.writeObject(output, object.getSelectItems());
            kryo.writeObject(output, object.getWhereCondition());
            kryo.writeObjectOrNull(output, object.getGroupByItems(), GroupByItems.class);
            kryo.writeObject(output, object.getLimitExpr());
        }

        @Override
        public DisDSLQueryBuilder read(Kryo kryo, Input input, Class<DisDSLQueryBuilder> type) {
            DisDSLQueryBuilder disDSLQueryBuilder = new DisDSLQueryBuilder();
            disDSLQueryBuilder.setSelectItems(kryo.readObject(input, SelectItems.class))
                    .setWhereCondition(kryo.readObject(input, WhereCondition.class))
                    .setGroupByItems(kryo.readObjectOrNull(input, GroupByItems.class))
                    .setLimitExpr(kryo.readObject(input, LimitExpr.class));
            return disDSLQueryBuilder;
        }

    }

}
