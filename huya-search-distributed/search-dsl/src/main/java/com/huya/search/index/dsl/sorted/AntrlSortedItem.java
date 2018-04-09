package com.huya.search.index.dsl.sorted;

import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.MetaEnum;
import org.apache.lucene.search.SortField;

public class AntrlSortedItem implements SortedItem {

    public static final SortField TIMESTAMP_DESC = new SortField(MetaEnum.TIMESTAMP, SortField.Type.LONG, true);

    public static AntrlSortedItem newInstance(MetaDefine metaDefine, String field, SortedWay way) {
        return new AntrlSortedItem(metaDefine, field, way);
    }

    private MetaDefine metaDefine;

    private String field;

    private SortedWay way;

    private AntrlSortedItem(MetaDefine metaDefine, String field, SortedWay way) {
        this.metaDefine = metaDefine;
        this.field = field;
        this.way   = way;
    }


    @Override
    public SortField getSortField() {
        return new SortField(field, getType(field), way == SortedWay.DESC);
    }

    private SortField.Type getType(String field) {
        return metaDefine.getIndexFieldFeatureType(field).getType().getSortType();
    }
}
