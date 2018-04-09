package com.huya.search.index.data.merger;

import com.huya.search.index.data.*;
import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMerger extends Merger {

    private List<String> groups;

    private MetaDefine metaDefine;

    private int from = 0;

    public GroupMerger(List<String> groups, MetaDefine metaDefine, int top) {
        super(top);
        this.groups = groups;
        this.metaDefine = metaDefine;
    }

    public GroupMerger(List<String> groups, MetaDefine metaDefine, int from, int to) {
        super(to + 1);
        this.groups = groups;
        this.metaDefine = metaDefine;
        this.from = from;
    }

    public GroupMerger(List<String> groups, MetaDefine metaDefine) {
        super();
        this.groups = groups;
        this.metaDefine = metaDefine;
    }

    @Override
    public GroupQueryResult<? extends Iterable<IndexableField>> result() {
        SearchData<? extends Iterable<IndexableField>> topDocs = super.result();
        Map<GroupKey, List<Iterable<IndexableField>>> map = new HashMap<>();
        for (int i=from; i<topDocs.size(); i++) {
            Iterable<IndexableField> document = topDocs.get(i);
            toBucket(map, document, groups, metaDefine);
        }

        List<GroupSearchData<Iterable<IndexableField>>> temp = new ArrayList<>();
        for (Map.Entry<GroupKey, List<Iterable<IndexableField>>> entry : map.entrySet()) {
            GroupSearchData<Iterable<IndexableField>> groupSearchData = new GroupSearchData<>(entry.getKey(), entry.getValue());
            temp.add(groupSearchData);
        }
        return new GroupQueryResult<>(temp);
    }

    static void toBucket(Map<GroupKey, List<Iterable<IndexableField>>> map, Iterable<IndexableField> document, List<String> groups, MetaDefine metaDefine) {
        GroupKey key = new RealGroupKey(groups, document, metaDefine);
        List<Iterable<IndexableField>> temp = map.get(key);
        if (temp != null) {
            temp.add(document);
        }
        else {
            temp = new ArrayList<>();
            temp.add(document);
            map.put(key, temp);
        }
    }
}
