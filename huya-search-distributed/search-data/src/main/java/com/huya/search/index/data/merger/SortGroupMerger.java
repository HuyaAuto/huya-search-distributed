package com.huya.search.index.data.merger;

import com.huya.search.index.data.*;
import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public class SortGroupMerger extends SortMerger {

    private List<String> groups;

    private MetaDefine metaDefine;

    public SortGroupMerger(Sort sort, List<String> groups, MetaDefine metaDefine, int top) {
       super(sort, top);
       this.groups = groups;
       this.metaDefine = metaDefine;
    }

    public SortGroupMerger(Sort sort, List<String> groups, MetaDefine metaDefine, int from, int to) {
        super(sort, from, to);
        this.groups = groups;
        this.metaDefine = metaDefine;
    }

    public SortGroupMerger(Sort sort, List<String> groups, MetaDefine metaDefine) {
        super(sort);
        this.groups = groups;
        this.metaDefine = metaDefine;
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> result() {
        SearchData<? extends Iterable<IndexableField>> topDocs = super.result();
        Map<GroupKey, List<Iterable<IndexableField>>> map = new HashMap<>();
        for (Iterable<IndexableField> document : topDocs.getCollection()) {
            GroupMerger.toBucket(map, document, groups, metaDefine);
        }
        List<GroupSearchData<Iterable<IndexableField>>> temp = new ArrayList<>();
        for (Map.Entry<GroupKey, List<Iterable<IndexableField>>> entry : map.entrySet()) {
            GroupSearchData<Iterable<IndexableField>> groupSearchData = new GroupSearchData<>(entry.getKey(), entry.getValue());
            temp.add(groupSearchData);
        }

        temp.sort(new Comparator<GroupSearchData<Iterable<IndexableField>>>() {
            @Override
            public int compare(GroupSearchData<Iterable<IndexableField>> o1, GroupSearchData<Iterable<IndexableField>> o2) {
                List<IndexableField> list1 = o1.getGroupKey().groupKeyFields();
                List<IndexableField> list2 = o2.getGroupKey().groupKeyFields();

                SortField[] fields = getSort().getSort();
                for (SortField field : fields) {
                    int reverse = field.getReverse() ? 1 : -1;
                    String fieldName = field.getField();
                    SortField.Type type = field.getType();

                    IndexableField aField = getField(fieldName, list1);

                    if (aField == null) continue;

                    IndexableField bField = getField(fieldName, list2);

                    int c = IndexableFieldCompare.compare(type, aField, bField, reverse);

                    if (c != 0) return -c;
                }

                return 0;
            }

            IndexableField getField(String name, List<IndexableField> list) {
                for (IndexableField field : list) {
                    if (Objects.equals(field.name(), name)) {
                        return field;
                    }
                }
                return null;
            }
        });

        return new GroupQueryResult<>(temp);
    }

}
