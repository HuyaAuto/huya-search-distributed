package com.huya.search.index;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.huya.search.index.data.function.Function;
import com.huya.search.index.dsl.group.AntrlGroupByItem;
import com.huya.search.index.dsl.group.AntrlGroupByItems;
import com.huya.search.index.dsl.group.GroupByItem;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.AntrlLimitExpr;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.*;
import com.huya.search.index.dsl.sorted.AntrlSortedItem;
import com.huya.search.index.dsl.sorted.AntrlSortedItems;
import com.huya.search.index.dsl.sorted.SortedItem;
import com.huya.search.index.dsl.sorted.SortedItems;
import com.huya.search.index.dsl.where.*;
import com.huya.search.index.dsl.where.expression.ExplainedExpression;
import com.huya.search.index.dsl.where.expression.TimestampExpression;
import com.huya.search.index.dsl.where.expression.WhereExpression;
import com.huya.search.index.data.function.AggrFun;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public class KryoSerializerInit {

    private static final Serializer<SelectItems> SELECT_ITEMS_SERIALIZER = new SelectItems.SelectItemsSerializer();

    private static final Serializer<SelectItem> SELECT_ITEM_SERIALIZER  = new SelectItem.SelectItemSerializer();

    private static final Serializer<FunctionSelectItem> FUNCTION_SELECT_ITEM_SERIALIZER = new FunctionSelectItem.FunctionSelectItemSerializer();

    private static final Serializer<Function> FUNCTION_SERIALIZER = new Function.FunctionSerializer();

    private static final Serializer<WhereCondition> WHERE_CONDITION_SERIALIZER = new WhereCondition.WhereConditionSerializer();

    private static final Serializer<WhereStatement> WHERE_STATEMENT_SERIALIZER = new WhereStatement.WhereStatementSerializer();

    private static final Serializer<WhereExpression> WHERE_EXPRESSION_SERIALIZER = new WhereExpression.WhereExpressionSerializer();

    private static final Serializer<GroupByItems> GROUP_BY_ITEMS_SERIALIZER = new GroupByItems.GroupByItemsSerializer();

    private static final Serializer<GroupByItem> GROUP_BY_ITEM_SERIALIZER = new GroupByItem.GroupByItemSerializer();

    private static final Serializer<LimitExpr> LIMIT_EXPR_SERIALIZER = new LimitExpr.LimitExprSerializer();

    private static final Serializer<SortedItems> SORTED_ITEMS_SERIALIZER = new SortedItems.SortedItemsSerializer();

    private static final Serializer<SortedItem> SORTED_ITEM_SERIALIZER = new SortedItem.SortedItemSerializer();

    public static void run(Kryo kryo) {
        kryo.register(SelectItems.class, SELECT_ITEMS_SERIALIZER);
        kryo.register(SelectItem.class, SELECT_ITEM_SERIALIZER);
        kryo.register(FunctionSelectItem.class, FUNCTION_SELECT_ITEM_SERIALIZER);
        kryo.register(Function.class, FUNCTION_SERIALIZER);
        kryo.register(WhereCondition.class, WHERE_CONDITION_SERIALIZER);
        kryo.register(WhereStatement.class, WHERE_STATEMENT_SERIALIZER);
        kryo.register(SingleWhereStatement.class, WHERE_STATEMENT_SERIALIZER);
        kryo.register(AndWhereStatement.class, WHERE_STATEMENT_SERIALIZER);
        kryo.register(OrWhereStatement.class, WHERE_STATEMENT_SERIALIZER);
        kryo.register(WhereExpression.class, WHERE_EXPRESSION_SERIALIZER);
        kryo.register(ExplainedExpression.class, WHERE_EXPRESSION_SERIALIZER);
        kryo.register(TimestampExpression.class, WHERE_EXPRESSION_SERIALIZER);
        kryo.register(GroupByItems.class, GROUP_BY_ITEMS_SERIALIZER);
        kryo.register(GroupByItem.class, GROUP_BY_ITEM_SERIALIZER);
        kryo.register(LimitExpr.class, LIMIT_EXPR_SERIALIZER);
        kryo.register(SortedItems.class, SORTED_ITEMS_SERIALIZER);
        kryo.register(SortedItem.class, SORTED_ITEM_SERIALIZER);
        kryo.register(AntrlSelectItems.class, SELECT_ITEMS_SERIALIZER);
        kryo.register(AntrlColumnSelectItem.class, SELECT_ITEM_SERIALIZER);
        kryo.register(AntrlFunctionSelectItem.class, FUNCTION_SELECT_ITEM_SERIALIZER);
        kryo.register(AggrFun.class, FUNCTION_SERIALIZER);
        kryo.register(AntrlWhereCondition.class, WHERE_CONDITION_SERIALIZER);
        kryo.register(AntrlGroupByItems.class, GROUP_BY_ITEMS_SERIALIZER);
        kryo.register(AntrlGroupByItem.class, GROUP_BY_ITEM_SERIALIZER);
        kryo.register(AntrlLimitExpr.class, LIMIT_EXPR_SERIALIZER);
        kryo.register(AntrlSortedItems.class, SORTED_ITEMS_SERIALIZER);
        kryo.register(AntrlSortedItem.class, SORTED_ITEM_SERIALIZER);
    }

}
