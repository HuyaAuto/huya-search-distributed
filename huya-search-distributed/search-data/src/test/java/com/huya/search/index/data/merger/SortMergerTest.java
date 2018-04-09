package com.huya.search.index.data.merger;

import com.huya.search.index.data.impl.PartitionDocs;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class SortMergerTest {

    private static String FIELD = "test";

    @Test
    public void runTest() {
        SortMerger sortMerger = new SortMerger(new Sort(new SortField(FIELD, SortField.Type.LONG)),5);
        sortMerger.add(PartitionDocs.newInstance("1", getList(5, 6, 9)));
        sortMerger.add(PartitionDocs.newInstance("2", getList(10, 11)));
        sortMerger.result().getCollection().forEach(it -> System.out.println(it.iterator().next().numericValue()));
    }


    List<? extends Iterable<IndexableField>> getList(long... vars) {
        List<Iterable<IndexableField>> temp = new ArrayList<>();

        for (long value : vars) {
            temp.add(() -> new Iterator<IndexableField>() {

                private boolean flag = true;

                @Override
                public boolean hasNext() {
                    return flag;
                }

                @Override
                public IndexableField next() {
                    flag = false;
                    return new LongPoint(FIELD, value);
                }
            });
        }

        return temp;
    }
}
