package com.huya.search.index.dsl.parse;

import com.huya.search.index.data.function.FunctionMap;
import com.huya.search.index.dsl.group.AntrlGroupByItem;
import com.huya.search.index.dsl.group.AntrlGroupByItems;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.AntrlLimitExpr;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.*;
import com.huya.search.index.dsl.sorted.AntrlSortedItem;
import com.huya.search.index.dsl.sorted.AntrlSortedItems;
import com.huya.search.index.dsl.sorted.SortedItems;
import com.huya.search.index.dsl.sorted.SortedWay;
import com.huya.search.index.dsl.where.AntrlWhereCondition;
import com.huya.search.index.dsl.where.WhereCondition;
import com.huya.search.index.dsl.where.WhereStatement;
import com.huya.search.index.dsl.where.expression.ExplainedExpression;
import com.huya.search.index.dsl.where.expression.ExpressionOperator;
import com.huya.search.index.dsl.where.expression.TimestampExpression;
import com.huya.search.index.meta.*;
import com.huya.search.index.data.function.AggrFun;
import com.huya.search.index.meta.impl.FieldFactory;
import com.huya.search.inject.ModulesBuilder;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.huya.search.index.dsl.parse.SearchSQLParser.*;
import static com.huya.search.index.dsl.parse.SearchSQLParser.CLOSE_PAR;
import static com.huya.search.index.dsl.parse.SearchSQLParser.OPEN_PAR;
import static com.huya.search.index.dsl.parse.SearchSQLParser.STAR;
import static com.huya.search.index.dsl.where.WhereStatementBuilder.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/8.
 */
public class SimpleSelectComponentFill implements ComponentFill {

    static {
        FunctionMap.functionMap.put(AggrFun.COUNT.functionName(), AggrFun.COUNT);
        FunctionMap.functionMap.put(AggrFun.SUM.functionName(), AggrFun.SUM);
        FunctionMap.functionMap.put(AggrFun.MAX.functionName(), AggrFun.MAX);
        FunctionMap.functionMap.put(AggrFun.MIN.functionName(), AggrFun.MIN);
    }

    public static ComponentFill newInstance(Simple_select_stmtContext sssc) {
        return new SimpleSelectComponentFill(sssc);
    }

    private Simple_select_stmtContext sssc;

    protected SimpleSelectComponentFill(Simple_select_stmtContext sssc) {
        this.sssc = sssc;
    }

    @Override
    public void fillComponent(QueryComponent component) {
        List<Result_columnContext> rccList = sssc.result_column();
        List<Table_nameContext>    tncList = sssc.table_name();
        Where_exprContext          wec     = sssc.where_expr();
        List<Group_columnContext>  gccList = sssc.group_column();
        List<Order_columnContext>  occList = sssc.order_column();
        LimitContext               lc      = sssc.limit();

        String table = parseTable(tncList);
        TimelineMetaDefine metaDefine = ModulesBuilder.getInstance()
                .createInjector().getInstance(MetaService.class).get(table);

        IntactMetaDefine lastMetaDefine = metaDefine.getLast();

        SelectItems    selectItems    = parseSelect(lastMetaDefine, rccList);

        WhereCondition whereCondition = AntrlWhereCondition.newInstance(metaDefine, parseWhere(wec));

        GroupByItems   groupByItems   = parseGroup(lastMetaDefine, gccList);

        SortedItems    sortedItems    = parseSort(lastMetaDefine, occList);

        LimitExpr      limitExpr      = parseLimit(lc, selectItems.hasAggrFunction(), groupByItems != null);

        checkWhereRange(whereCondition);

        checkQueryNewWithTimestamp(selectItems, sortedItems);

        checkSelectAndGroup(selectItems, groupByItems);

        checkSortedAndLimit(sortedItems, limitExpr, selectItems);

        checkAggrAndGroupBy(selectItems, groupByItems);

        component.setTable(table)
                .setSelectItems(selectItems)
                .setWhereCondition(whereCondition)
                .setGroupByItems(groupByItems)
                .setSortedItems(sortedItems)
                .setLimitExpr(limitExpr);
    }

    /**
     * 如果有聚合函数，并且分组信息
     * @param selectItems
     * @param groupByItems
     */
    private void checkAggrAndGroupBy(SelectItems selectItems, GroupByItems groupByItems) {
        if (selectItems.hasAggrFunction() && !selectItems.hasColumn() && groupByItems != null) {

            if (groupByItems.groupFields().size() > 1 || !groupByItems.groupFields().get(0).startsWith(MetaEnum.GRAIN)) {
                throw new ParseExpection("no support query aggr with other column or group by");
            }
        }
    }

    /**
     * 检查查询最新数据时是否制定需要提取 timestamp 字段，没有自动添加上去
     * @param selectItems select 部分
     * @param sortedItems order by 部分
     */
    private void checkQueryNewWithTimestamp(SelectItems selectItems, SortedItems sortedItems) {
        if (sortedItems!= null && sortedItems.onlySortByNewDoc()) {
            if (selectItems.getColumnList().stream().noneMatch(selectItem -> Objects.equals(selectItem.getField(), MetaEnum.TIMESTAMP))) {
                selectItems.addColumn(AntrlColumnSelectItem.newInstance(
                        MetaEnum.TIMESTAMP, MetaEnum.TIMESTAMP, MetaEnum.TIMESTAMP,
                        IndexFieldType.Long,
                        selectItems.size()
                ));
            }
        }
    }

    private void checkWhereRange(WhereCondition whereCondition) {
        Set<Long> longs = whereCondition.getCycleList();
        if (longs.size() > 24) {
            throw new ParseExpection("no support query range more than 24 partition " + StringUtils.join(longs, ","));
        }
    }

    private void checkSortedAndLimit(SortedItems sortedItems, LimitExpr limitExpr, SelectItems selectItems) {
        if (sortedItems != null && limitExpr.selectAll() && selectItems.getAggrFunctionSelectItemList().size() == 0) {
            throw new ParseExpection("no support sort all result, must contain a limit expr");
        }
    }

    private void checkSelectAndGroup(SelectItems selectItems, GroupByItems groupByItems) {
        if (!selectItems.selectTableAllColumn() && groupByItems != null) {
            for (String field : groupByItems.groupFields()) {
                if (!selectItems.columnsContainsField(field)) {
                    throw new ParseExpection("group by item no exist in select item");
                }
            }
        }
    }

    private String parseTable(List<Table_nameContext> tncList) {
        if (tncList.size() == 0) throw new ParseExpection("miss table");
        else if (tncList.size() > 1) throw new ParseExpection("no support multi table or miss table");

        Table_nameContext tnc = tncList.get(0);
        return tnc.getText();
    }

    private SelectItems parseSelect(IntactMetaDefine intactMetaDefine, List<Result_columnContext> rccList) {
        assert rccList.size() > 0;
        if (rccList.size() == 1) {
            Result_columnContext rcc = rccList.get(0);
            if (rcc.start.getType() == rcc.stop.getType() &&
                    rcc.start.getType() == STAR) {
                return AntrlSelectItems.newSelectTableAllColumnInstance(intactMetaDefine);
            }
        }

        SelectItems selectItems = AntrlSelectItems.newInstance(intactMetaDefine);

        AutoField autoField = new AutoField();

        rccList.forEach(rcc ->  {
            Function_exprContext fec = rcc.function_expr();
            if (fec != null) {
                selectItems.addFunction(parseFunctionSelectItem(intactMetaDefine, rcc, fec, autoField));
            }
            else {
                selectItems.addColumn(parseColumnSelectItem(intactMetaDefine, rcc, autoField));
            }
        });
        return selectItems;
    }

    @SuppressWarnings("unchecked")
    private FunctionSelectItem parseFunctionSelectItem(MetaDefine metaDefine, Result_columnContext rcc, Function_exprContext fec, AutoField autoField) {
        Column_aliasContext cac = rcc.column_alias();
        String alias = cac == null ? null : cac.getText();

        Function_nameContext fnc = fec.function_name();
        AggrFun aggrFun = AggrFun.get(fnc.getText());
        List<Column_nameContext> cncList = fec.column_name();
        String expr = rcc.getText();
        int num = autoField.getNum();
        String field = autoField.getField();

        try {
            if (cncList == null || cncList.size() == 0) {
                IndexFieldType indexFieldType = aggrFun.resultIndexFieldType(null);

                return AntrlFunctionSelectItem.newInstance(field, alias, expr, indexFieldType, num, aggrFun, Collections.EMPTY_LIST);
            } else {
                AutoField innerAutoField = new AutoField();
                List<SelectItem> temp = new ArrayList<>();
                cncList.forEach(cnc -> temp.add(parseSelectItem(metaDefine, cnc, innerAutoField)));
                IndexFieldType indexFieldType = aggrFun.resultIndexFieldType(
                        temp.stream().map(ResultType::type).collect(Collectors.toList())
                );

                return AntrlFunctionSelectItem.newInstance(field, alias, expr, indexFieldType, num, aggrFun, temp);
            }
        } finally {
            autoField.increase();
        }
    }


    private SelectItem parseColumnSelectItem(MetaDefine metaDefine, Result_columnContext rcc, AutoField autoField) {
        Column_nameContext cnc = rcc.column_name();
        String field = cnc.getText();
        Column_aliasContext cac = rcc.column_alias();
        String alias = cac == null ? null : cac.getText();
        String expr = rcc.getText();
        IndexFieldType indexFieldType = metaDefine.getIndexFieldFeatureType(field).getType();
        int num = autoField.getNum();
        try {
            return AntrlColumnSelectItem.newInstance(field, alias, expr, indexFieldType, num);
        } finally {
            autoField.increase();
        }
    }

    private SelectItem parseSelectItem(MetaDefine metaDefine, Column_nameContext cnc, AutoField autoField) {
        String field = cnc.getText();
        IndexFieldType indexFieldType = metaDefine.getIndexFieldFeatureType(field).getType();
        int num = autoField.getNum();
        return AntrlColumnSelectItem.newInstance(field, null, field, indexFieldType, num);
    }


    private WhereStatement parseWhere(Where_exprContext wec) {
        if (wec == null) throw new ParseExpection("miss where condition");

        if (wec.start.getType() == OPEN_PAR && wec.stop.getType() == CLOSE_PAR) {
            return parseWhere(wec.where_expr(0));
        }
        else if (wec.K_AND() != null) {
            return parseAndWhere(wec);
        }
        else if (wec.K_OR() != null) {
            return parseOrWhere(wec);
        }
        else {
            return parseSingleWhere(wec);
        }
    }

    private WhereStatement parseAndWhere(Where_exprContext wec) {
        List<Where_exprContext> wecList = wec.where_expr();
        AndBuilder builder = AndBuilder.newInstance();
        for (Where_exprContext wecTemp : wecList) {
            builder.addWhereStatement(parseWhere(wecTemp));
        }
        return builder.build();
    }

    private WhereStatement parseOrWhere(Where_exprContext wec) {
        List<Where_exprContext> wecList = wec.where_expr();
        OrBuilder builder = OrBuilder.newInstance();
        for (Where_exprContext wecTemp : wecList) {
            builder.addWhereStatement(parseWhere(wecTemp));
        }
        return builder.build();
    }

    private WhereStatement parseSingleWhere(Where_exprContext wec) {
        Column_nameContext      cnc = wec.column_name();
        Current_operatorContext coc = wec.current_operator();
        Literal_valueContext    lvc = wec.literal_value();
        String columnNam = cnc.getText();
        ExpressionOperator operator = ExpressionOperator.operator(coc.getText());
        String value = LiteralValueParse.getString(lvc);
        if (Objects.equals(columnNam, TimestampExpression.PR) || Objects.equals(columnNam, FieldFactory.TIME_STAMP)) {
            return Single.build(TimestampExpression.newInstance(operator, value));
        }
        else {
            return Single.build(ExplainedExpression.newInstance(columnNam, operator, value));
        }
    }


    private GroupByItems parseGroup(IntactMetaDefine intactMetaDefine, List<Group_columnContext> gccList) {
        if (gccList.size() == 0) {
            return null;
        }
        else {
            AntrlGroupByItems groupByItems = AntrlGroupByItems.newInstance(intactMetaDefine);
            for (Group_columnContext gcc : gccList) {
                groupByItems.addGroupItem(AntrlGroupByItem.newInstance(gcc.getText()));
            }
            return groupByItems;
        }
    }

    private SortedItems parseSort(MetaDefine metaDefine, List<Order_columnContext> occList) {
        if (occList.size() == 0) {
            return null;
        }
        else {
            AntrlSortedItems sortedItems = AntrlSortedItems.newInstance();
            for (Order_columnContext occ : occList) {
                Column_nameContext cnc = occ.column_name();

                String sortFieldName = cnc.getText();

                if (Objects.equals(sortFieldName.toLowerCase(), SortedItems.NEW_DOC)) {
                    sortedItems.setNewDoc(true);
                    continue;
                }

                boolean asc  = occ.K_ASC() != null;
                boolean desc = occ.K_DESC() != null;
                SortedWay way = !asc && !desc
                        ? SortedWay.DESC
                        : asc ? SortedWay.ASC : SortedWay.DESC;
                sortedItems.addSortedItem(AntrlSortedItem.newInstance(metaDefine, sortFieldName, way));
            }
            return sortedItems;
        }
    }

    private static final int DEFAULT_LIMIT_NUM = 2000;

    private LimitExpr parseLimit(LimitContext lc, boolean hasAggr, boolean hasGroupBy) {
        if (lc != null) {
            List<TerminalNode> bound = lc.NUMERIC_LITERAL();
            if (bound == null || bound.size() == 0) {
                if (hasAggr && !hasGroupBy) {               //聚合查询并且没有分组要求不限制查询数量
                    return AntrlLimitExpr.newInstance();
                }
                else {
                    return AntrlLimitExpr.newInstance(DEFAULT_LIMIT_NUM);
                }
            } else if (bound.size() == 1) {
                int top = Integer.parseInt(bound.get(0).getText());

                if (top <= 0) {
                    throw new ParseExpection("top can not be less than or equal to 0");
                }

                if (top > DEFAULT_LIMIT_NUM) {
                    throw new ParseExpection("top can not be more than " + DEFAULT_LIMIT_NUM + ", you can use limit [from] [len] api to replace");
                }

                return AntrlLimitExpr.newInstance(top);
            } else if (bound.size() == 2) {
                int from = Integer.parseInt(bound.get(0).getText());
                int len = Integer.parseInt(bound.get(1).getText());

                if (from < 0) {
                    throw new ParseExpection("limit from = " + from + " can not be less than 0");
                }

                if (len <= 0) {
                    throw new ParseExpection("limit len = " + len + " can not be less than or equal to 0");
                }

                if (len > DEFAULT_LIMIT_NUM) {
                    throw new ParseExpection("limit len = " + len + " can not be more than or equal to " + DEFAULT_LIMIT_NUM);
                }

                return AntrlLimitExpr.newInstance(from, len);
            }
            throw new ParseExpection("limit More parameters");
        }
        else {
            if (hasAggr && !hasGroupBy) {
                return AntrlLimitExpr.newInstance();
            }
            else {
                return AntrlLimitExpr.newInstance(DEFAULT_LIMIT_NUM);
            }
        }
    }

    private class AutoField {

        private int i = 0;

        private String getField() {
            return "[" + i + "]";
        }

        public int getNum() {
            return i;
        }

        void increase() {
            i++;
        }

    }

}
