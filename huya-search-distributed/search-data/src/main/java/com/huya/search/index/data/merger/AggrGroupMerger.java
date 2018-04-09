package com.huya.search.index.data.merger;

import com.huya.search.index.data.GroupKey;
import com.huya.search.index.data.GroupQueryResult;
import com.huya.search.index.data.GroupSearchData;
import com.huya.search.index.data.SearchData;
import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/21.
 *
 * 分组进行聚合
 */
public class AggrGroupMerger extends GroupMerger {

    private DefaultAggrFunUnitSet defaultAggrFunUnitSet;

    public AggrGroupMerger(List<String> groups, MetaDefine metaDefine, DefaultAggrFunUnitSet defaultAggrFunUnitSet) {
        super(groups, metaDefine);
        this.defaultAggrFunUnitSet = defaultAggrFunUnitSet;
    }

    @Override
    public GroupQueryResult<? extends Iterable<IndexableField>> result() {
        List<GroupSearchData<Iterable<IndexableField>>> result = new ArrayList<>();

        GroupQueryResult<? extends Iterable<IndexableField>> groupQueryResult = super.result();
        int groupSize = groupQueryResult.groupSize();

        for (int i = 0; i <groupSize; i++) {
            SearchData<? extends Iterable<IndexableField>> searchData = groupQueryResult.getGroup(i);
            GroupKey groupKey = groupQueryResult.getGroupKey(i);
            searchData.getCollection().forEach(iterable -> {
                iterable.forEach(indexableField -> {
                    defaultAggrFunUnitSet.add(indexableField);
                });
            });
            defaultAggrFunUnitSet.addGroupBy(groupKey.groupKeyFields());
            GroupSearchData<Iterable<IndexableField>> groupSearchData = new GroupSearchData<>(groupKey, defaultAggrFunUnitSet.result().getCollection());
            result.add(groupSearchData);
            defaultAggrFunUnitSet.clear();
        }
        return new GroupQueryResult<>(result);
    }
}
