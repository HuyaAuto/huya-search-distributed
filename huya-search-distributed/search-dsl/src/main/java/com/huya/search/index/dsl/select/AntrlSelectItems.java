package com.huya.search.index.dsl.select;

import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.IntactMetaDefine;

import java.util.Map;


/**
 * Created by zhangyiqun1@yy.com on 2017/9/11.
 */
public class AntrlSelectItems extends SelectItems {

    public static AntrlSelectItems newSelectTableAllColumnInstance(IntactMetaDefine intactMetaDefine) {
        return new AntrlSelectItems(intactMetaDefine, true);
    }

    public static AntrlSelectItems newInstance(IntactMetaDefine intactMetaDefine) {
        return new AntrlSelectItems(intactMetaDefine, false);
    }

    private boolean selectTableAllColumn;

    private AntrlSelectItems(IntactMetaDefine intactMetaDefine, boolean selectTableAllColumn) {
        super(intactMetaDefine);
        this.selectTableAllColumn = selectTableAllColumn;
        if (selectTableAllColumn) {
            initSelectTableAllColumn();
        }
    }

    private void initSelectTableAllColumn() {
        int i = 0;
        for (Map.Entry<String, IndexFeatureType> entry : getMetaDefine().getAll().entrySet()) {
            String field = entry.getKey();
            IndexFieldType indexFieldType = entry.getValue().getType();
            SelectItem selectItem = AntrlColumnSelectItem.newInstance(field, null, field, indexFieldType, i++);
            addColumn(selectItem);
        }
    }

    @Override
    public boolean selectTableAllColumn() {
        return selectTableAllColumn;
    }

    @Override
    public String getTable() {
        return getMetaDefine().getTable();
    }
}
