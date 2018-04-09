package com.huya.search.data.util;

import com.huya.search.index.data.merger.IndexableFieldCompare;
import com.huya.search.index.data.SearchData;
import com.huya.search.index.data.util.DataConvert;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/10.
 */
public class DataAssert {

    /**
     * 只对比数据内容，不考虑数据顺序
     * @param aData a SearchData
     * @param bData b SearchData
     * @return 内容是否相同
     */
    public static boolean equal(SearchData<? extends Iterable<IndexableField>> aData, SearchData<? extends Iterable<IndexableField>> bData, SortField[] fields) {
        List<? extends Iterable<IndexableField>> aList = aData.getCollection();
        List<? extends Iterable<IndexableField>> bList = bData.getCollection();
        if (aList.size() != bList.size()) return false;

        Set<String> aFieldSet = getFieldSet(aList);
        Set<String> bFieldSet = getFieldSet(bList);

        if (aFieldSet.equals(bFieldSet) && bFieldSet.equals(aFieldSet)) {
            fields = filter(fields, aFieldSet);
            Comparator<Iterable<IndexableField>> comparator = new ComparatorIndexabField(fields);
            aList.sort(comparator);
            bList.sort(comparator);
            return equal(aList, bList);
        }
        return false;
    }

    public static Set<String> getFieldSet(List<? extends Iterable<IndexableField>> list) {
        Set<String> set = new HashSet<>();
        list.forEach(iterable -> iterable.forEach(field -> set.add(field.name())));
        return set;
    }

    private static SortField[] filter(SortField[] fields, Set<String> set) {
        List<SortField> temp = new ArrayList<>();
        set.forEach(str -> {
            for (SortField field : fields) {
                if (field.getField().equals(str)) {
                    temp.add(field);
                }
            }
        });
        SortField[] sortFields = new SortField[temp.size()];
        return temp.toArray(sortFields);
    }

    /**
     * 按顺序对比数据内容
     * @param aData a SearchData
     * @param bData b SearchData
     * @return 内容是否相同
     */
    public static boolean equalWithOrder(SearchData<? extends Iterable<IndexableField>> aData, SearchData<? extends Iterable<IndexableField>> bData) {
        List<? extends Iterable<IndexableField>> aList = aData.getCollection();
        List<? extends Iterable<IndexableField>> bList = bData.getCollection();
        return aList.size() == bList.size() && equal(aList, bList);

    }

    private static boolean equal(List<? extends Iterable<IndexableField>> aList, List<? extends Iterable<IndexableField>> bList) {
        for (int i = 0; i < aList.size(); i++) {
            if (!equal(aList.get(i), bList.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean equal(Iterable<IndexableField> aIterable, Iterable<IndexableField> bIterable) {
        Map<String, ?> aMap = DataConvert.rowToMap(aIterable);
        Map<String, ?> bMap = DataConvert.rowToMap(bIterable);
        return AssertMap.equals(aMap, bMap);
    }

    private static class ComparatorIndexabField implements Comparator<Iterable<IndexableField>> {

        private SortField[] fields;

        ComparatorIndexabField(SortField[] fields) {
            this.fields = fields;
        }

        @Override
        public int compare(Iterable<IndexableField> o1, Iterable<IndexableField> o2) {
            Map<String, IndexableField> aMap = new HashMap<>();
            o1.forEach(field -> aMap.put(field.name(), field));

            Map<String, IndexableField> bMap = new HashMap<>();
            o2.forEach(field -> bMap.put(field.name(), field));

            for (SortField field : fields) {
                String fieldName = field.getField();
                IndexableField a = aMap.get(fieldName);
                IndexableField b = bMap.get(fieldName);

                if (a != null && b != null) {
                    int c = IndexableFieldCompare.compare(field.getType(), a, b, 1);
                    if (c != 0) return c;
                }
                else if (a == null && b != null) return -1;
                else if (a != null) return 1;
            }
            return 0;
        }
    }

}
