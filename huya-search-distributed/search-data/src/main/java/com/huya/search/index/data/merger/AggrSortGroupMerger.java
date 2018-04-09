package com.huya.search.index.data.merger;

import com.huya.search.index.data.GroupKey;
import com.huya.search.index.data.GroupQueryResult;
import com.huya.search.index.data.GroupSearchData;
import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.impl.PartitionDocs;
import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class AggrSortGroupMerger extends AggrGroupMerger {

    private Sort sort;

    public AggrSortGroupMerger(DefaultAggrFunUnitSet defaultAggrFunUnitSet, Sort sort, List<String> groups, MetaDefine metaDefine) {
        super(groups, metaDefine, defaultAggrFunUnitSet);
        this.sort = sort;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GroupQueryResult<? extends Iterable<IndexableField>> result() {
        List<GroupSearchData<? extends Iterable<IndexableField>>> result = new ArrayList<>();

        GroupQueryResult<? extends Iterable<IndexableField>>  groupQueryResult = super.result();
        int groupSize = groupQueryResult.groupSize();

        for (int i = 0; i < groupSize; i++) {
            SearchData<? extends Iterable<IndexableField>> searchData = groupQueryResult.getGroup(i);
            GroupKey groupKey = groupQueryResult.getGroupKey(i);

            SortMerger sortMerger = new SortMerger(sort);

            sortMerger.add(PartitionDocs.newInstance(String.valueOf(i), searchData.getCollection()));

            GroupSearchData<? extends Iterable<IndexableField>> groupSearchData = new GroupSearchData<>(groupKey, sortMerger.result().getCollection());

            result.add(groupSearchData);
        }
        return new GroupQueryResult(result);
    }
}
