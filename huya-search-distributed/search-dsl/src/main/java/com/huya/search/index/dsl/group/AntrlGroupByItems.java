package com.huya.search.index.dsl.group;

import com.huya.search.index.data.RealGroupKey;
import com.huya.search.index.data.GroupKey;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.index.IndexableField;

public class AntrlGroupByItems extends GroupByItems {

    public static AntrlGroupByItems newInstance(IntactMetaDefine intactMetaDefine) {
        return new AntrlGroupByItems(intactMetaDefine);
    }

    private MetaDefine intactMetaDefine;

    private AntrlGroupByItems(IntactMetaDefine intactMetaDefine) {
        super(intactMetaDefine.getTable());
        this.intactMetaDefine = intactMetaDefine;
    }

    @Override
    public GroupKey key(Iterable<IndexableField> document) {
        return new RealGroupKey(groupFields(), document, intactMetaDefine);
    }


}
