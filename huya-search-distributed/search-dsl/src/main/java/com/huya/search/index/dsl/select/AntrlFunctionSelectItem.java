package com.huya.search.index.dsl.select;

import com.huya.search.index.data.function.Function;
import com.huya.search.index.meta.IndexFieldType;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/21.
 */
public class AntrlFunctionSelectItem extends AntrlColumnSelectItem implements FunctionSelectItem {

    public static AntrlFunctionSelectItem newInstance(String field, String alias, String expr, IndexFieldType indexFieldType,
                                                      int number, Function function, List<SelectItem> selectItems) {
        return new AntrlFunctionSelectItem(field, alias, expr, indexFieldType, number, function, selectItems);
    }

    private Function function;

    private List<SelectItem> selectItems;

    private AntrlFunctionSelectItem(String field, String alias, String expr, IndexFieldType indexFieldType, int number, Function function, List<SelectItem> selectItems) {
        super(field, alias, expr, indexFieldType, number);
        this.function = function;
        this.selectItems = selectItems;
    }

    @Override
    public Function getFunction() {
        return function;
    }

    @Override
    public List<SelectItem> getAttrColumn() {
        return selectItems;
    }
}
