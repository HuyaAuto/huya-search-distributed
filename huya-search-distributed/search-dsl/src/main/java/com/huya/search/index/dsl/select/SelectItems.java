package com.huya.search.index.dsl.select;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.merger.DefaultAggrFunUnitSet;
import com.huya.search.index.data.merger.AggrFunSetBuilder;
import com.huya.search.index.data.merger.DefaultAggrFunSetBuilder;
import com.huya.search.index.data.merger.NodeAggrFunSetBuilder;
import com.huya.search.index.data.function.FunctionType;
import com.huya.search.index.meta.*;
import com.huya.search.index.data.function.AggrFun;
import com.huya.search.inject.ModulesBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public abstract class SelectItems implements CorrespondTable {

    /**
     * 查询项列表
     */
    private List<SelectItem> selectItemList = new ArrayList<>();

    /**
     * 查询字段列表
     */
    private List<SelectItem> columnList = new ArrayList<>();

    /**
     * 查询函数列表
     */
    private List<FunctionSelectItem> functionSelectItemList = new ArrayList<>();

    private IntactMetaDefine intactMetaDefine;

    protected SelectItems(IntactMetaDefine intactMetaDefine) {
        this.intactMetaDefine = intactMetaDefine;
    }

    public IntactMetaDefine getMetaDefine() {
        return intactMetaDefine;
    }

    public abstract boolean selectTableAllColumn();

    public void addColumn(SelectItem selectItem) {
        selectItemList.add(selectItem);
        columnList.add(selectItem);
    }

    public void addFunction(FunctionSelectItem functionSelectItem) {
        selectItemList.add(functionSelectItem);
        functionSelectItemList.add(functionSelectItem);
    }

    public boolean allItemIsColumn() {
        return functionSelectItemList.size() == 0;
    }

    public boolean allItemIsFunction() {
        return columnList.size() == 0;
    }

    public boolean hasFunction() {
        return functionSelectItemList.size() > 0;
    }

    public boolean hasColumn() {
        return columnList.size() > 0;
    }

    public boolean hasAggrFunction() {
        return getAggrFunctionSelectItemList().size() > 0;
    }

    public int size() {
        return selectItemList.size();
    }

    public Set<String> getAllFields() {
        return getAll(AsField::getField);
    }

    public Set<String> getColumnFields() {
        return getColumn(AsField::getField);
    }

    public Set<String> getFunctionFields() {
        return getFunction(AsField::getField);
    }

    public Set<String> getAllAlias() {
        return getAll(Alias::getAlias);
    }

    public Set<String> getColumnAlias() {
        return getColumn(Alias::getAlias);
    }

    public Set<String> getFunctionAlias() {
        return getFunction(Alias::getAlias);
    }

    public Set<String> getAllExpr() {
        return getAll(Expr::getExpr);
    }

    public Set<String> getColumnExpr() {
        return getColumn(Expr::getExpr);
    }

    public Set<String> getFunctionExpr() {
        return getFunction(Expr::getExpr);
    }

    private Set<IndexFieldType> getAllIndexFieldType() {
        return getAll(ResultType::type);
    }

    private Set<IndexFieldType> getColumnIndexFieldType() {
        return getColumn(ResultType::type);
    }

    private Set<IndexFieldType> getFunctionIndexFieldTyoe() {
        return getFunction(ResultType::type);
    }

    private <T> Set<T> getAll(Function<SelectItem, T> function) {
        return selectItemList.stream()
                .map(function)
                .collect(Collectors.toSet());
    }

    private <T> Set<T> getColumn(Function<SelectItem, T> function) {
        return columnList.stream()
                .map(function)
                .collect(Collectors.toSet());
    }

    private <T> Set<T> getFunction(Function<FunctionSelectItem, T> function) {
        return functionSelectItemList.stream()
                .map(function)
                .collect(Collectors.toSet());
    }

    public boolean columnsContainsField(String field) {
        return columnList.stream().anyMatch(selectItem -> selectItem.getField().equals(field));
    }

    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    public List<SelectItem> getColumnList() {
        return columnList;
    }

    public List<FunctionSelectItem> getFunctionSelectItemList() {
        return functionSelectItemList;
    }

    public List<FunctionSelectItem> getAggrFunctionSelectItemList() {
        return functionSelectItemList.stream()
                .filter(functionSelectItem -> functionSelectItem.getFunction().getFunctionType() == FunctionType.AGGR)
                .collect(Collectors.toList());
    }

    public Set<AggrFun> getAggrFunTypeSet() {
        return functionSelectItemList.stream()
                .filter(functionSelectItem -> functionSelectItem.getFunction().getFunctionType() == FunctionType.AGGR)
                .map(functionSelectItem -> (AggrFun) functionSelectItem.getFunction())
                .collect(Collectors.toSet());
    }

    public List<FunctionSelectItem> getCountSelectItemList() {
        return functionSelectItemList.stream()
                .filter(functionSelectItem -> {
                    com.huya.search.index.data.function.Function function = functionSelectItem.getFunction();
                    return function == AggrFun.COUNT;
                })
                .collect(Collectors.toList());
    }

    public List<FunctionSelectItem> getAggrFunctionNoCountSelectItemList() {
        return functionSelectItemList.stream()
                .filter(functionSelectItem -> {
                    com.huya.search.index.data.function.Function function = functionSelectItem.getFunction();
                    return function != AggrFun.COUNT && function.getFunctionType() == FunctionType.AGGR;
                })
                .collect(Collectors.toList());
    }

    /**
     * 只有直接查询函数（COUNT / MAX / MIN 这些是通过 Lucene 可以直接查询的函数，称为直接查询函数，不需要获取到数据的真实值）
     * @return 是否只有直接查询函数
     */
    public boolean onlyDirectFunction() {
        return functionSelectItemList.size() > 0 &&
                functionSelectItemList.stream()
                        .allMatch(functionSelectItem -> {
                            com.huya.search.index.data.function.Function function = functionSelectItem.getFunction();
                            return function == AggrFun.COUNT || function == AggrFun.MAX || function == AggrFun. MIN;
                        });
    }

    public boolean onlyCount() {
        return functionSelectItemList.size() > 0 &&
                functionSelectItemList.stream()
                        .noneMatch(functionSelectItem -> functionSelectItem.getFunction() != AggrFun.COUNT);
    }

    public DefaultAggrFunUnitSet getAggrFunSet() {
        AggrFunSetBuilder aggrFunSetBuilder = DefaultAggrFunSetBuilder.newInstance();

        List<SelectItem> columnSelectItemList = getColumnList();

        List<FunctionSelectItem> aggrFunctionNoCountSelectItemList = getAggrFunctionNoCountSelectItemList();

        List<FunctionSelectItem> countSelectItemList = getCountSelectItemList();

//        if ()

        aggrFunctionNoCountSelectItemList.forEach(functionSelectItem -> addFunctionSelectItemToDefaultBuilder(functionSelectItem, aggrFunSetBuilder));

        //随便取一个字段，计算 COUNT 并不关心是那个字段
        String oneField = aggrFunctionNoCountSelectItemList.size() > 0
                ? aggrFunctionNoCountSelectItemList.get(0).getAttrColumn().get(0).getField()
                : columnSelectItemList.size() > 0 ? columnSelectItemList.get(0).getField() : "*";

        countSelectItemList.forEach(fst -> aggrFunSetBuilder.add(oneField, fst.getField(), (AggrFun) fst.getFunction(), fst.type()));

        return aggrFunSetBuilder.build();
    }

    public DefaultAggrFunUnitSet getNodeAggrFunSet() {
        NodeAggrFunSetBuilder nodeAggrFunSetBuilder = NodeAggrFunSetBuilder.newInstance();

        List<FunctionSelectItem> functionSelectItemList = getFunctionSelectItemList();

        functionSelectItemList.forEach(functionSelectItem -> addFunctionSelectItemToNodeBuilder(functionSelectItem, nodeAggrFunSetBuilder));

        return nodeAggrFunSetBuilder.build();
    }

    public Set<String> getCountFields() {
        return functionSelectItemList.stream()
                .filter(functionSelectItem -> functionSelectItem.getFunction() == AggrFun.COUNT)
                .map(AsField::getField).collect(Collectors.toSet());
    }

    private void addFunctionSelectItemToDefaultBuilder(FunctionSelectItem selectItem, AggrFunSetBuilder aggrFunSetBuilder) {
        com.huya.search.index.data.function.Function function = selectItem.getFunction();
        assert function.getFunctionType() == FunctionType.AGGR;

        AggrFun aggrFun = (AggrFun) function;

        List<SelectItem> columnAttr = selectItem.getAttrColumn();

        assert columnAttr.size() == 1;

        SelectItem attr = columnAttr.get(0);

        aggrFunSetBuilder.add(attr.getField(), selectItem.getField(), aggrFun, selectItem.type());
    }

    private void addFunctionSelectItemToNodeBuilder(FunctionSelectItem selectItem, AggrFunSetBuilder aggrFunSetBuilder) {
        com.huya.search.index.data.function.Function function = selectItem.getFunction();
        assert function.getFunctionType() == FunctionType.AGGR;

        AggrFun aggrFun = (AggrFun) function;

        aggrFunSetBuilder.add(selectItem.getField(), selectItem.getField(), aggrFun, selectItem.type());
    }


    public static class SelectItemsSerializer extends Serializer<SelectItems> {

        @Override
        public void write(Kryo kryo, Output output, SelectItems object) {
            kryo.writeObject(output, object.getTable());

            boolean selectTableAllColumn = object.selectTableAllColumn();

            kryo.writeObject(output, selectTableAllColumn);

            if (!selectTableAllColumn) {

                int columnSize = object.getColumnList().size();
                int functionSize = object.getFunctionSelectItemList().size();

                kryo.writeObject(output, columnSize);
                kryo.writeObject(output, functionSize);

                if (columnSize > 0) {
                    object.columnList.forEach(selectItem -> kryo.writeObject(output, selectItem));
                }

                if (functionSize > 0) {
                    object.functionSelectItemList.forEach(functionSelectItem -> kryo.writeObject(output, functionSelectItem));
                }
            }
        }

        @Override
        public SelectItems read(Kryo kryo, Input input, Class<SelectItems> type) {
            String table = kryo.readObject(input, String.class);

            TimelineMetaDefine metaDefine = ModulesBuilder.getInstance()
                    .createInjector().getInstance(MetaService.class).get(table);

            boolean selectTableAllColumn = kryo.readObject(input, Boolean.class);

            if (selectTableAllColumn) {
                return AntrlSelectItems.newSelectTableAllColumnInstance(metaDefine.getLast());
            }
            else {
                SelectItems selectItems = AntrlSelectItems.newInstance(metaDefine.getLast());


                int columnSize = kryo.readObject(input, Integer.class);
                int functionSize = kryo.readObject(input, Integer.class);

                for (int i = 0; i < columnSize; i++) {
                    selectItems.addColumn(kryo.readObject(input, SelectItem.class));
                }

                for (int i = 0; i < functionSize; i++) {
                    selectItems.addFunction(kryo.readObject(input, FunctionSelectItem.class));
                }
                return selectItems;
            }
        }

    }

}
