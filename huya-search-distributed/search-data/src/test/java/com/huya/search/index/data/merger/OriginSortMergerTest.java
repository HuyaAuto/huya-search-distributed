package com.huya.search.index.data.merger;

import com.huya.search.index.data.impl.PartitionDocs;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Test;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class OriginSortMergerTest extends SortMergerTest {

    private static String FIELD = "test";

    @Test
    public void runTest() {
        OriginSortMerger sortMerger = new OriginSortMerger(new Sort(new SortField(FIELD, SortField.Type.LONG, true)),5);
        sortMerger.add(PartitionDocs.newInstance("1", getList(12, 5, 6, 9)));
        sortMerger.add(PartitionDocs.newInstance("2", getList(10, 11)));
        sortMerger.result().getCollection().forEach(it -> System.out.println(it.iterator().next().numericValue()));

    }
}
