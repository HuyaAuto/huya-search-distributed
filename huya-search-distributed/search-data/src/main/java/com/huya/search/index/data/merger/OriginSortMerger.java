package com.huya.search.index.data.merger;

import com.huya.search.index.data.impl.DefaultPartitionDoc;
import com.huya.search.index.data.impl.PartitionDocs;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Sort;

import java.util.Comparator;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/24.
 */
public class OriginSortMerger extends SortMerger {

    public OriginSortMerger(Sort sort, int top) {
        super(sort, top);
    }

    public OriginSortMerger(Sort sort, int from, int to) {
        super(sort, from, to);
    }

    public OriginSortMerger(Sort sort) {
        super(sort);
    }

    @Override
    public void add(PartitionDocs partitionDocs) {
        if (!isFreeze()) {
            originSort(partitionDocs);
            super.add(partitionDocs);
        }
    }

    private void originSort(PartitionDocs partitionDocs) {
        partitionDocs.getDocumentList().sort((Comparator<Iterable<IndexableField>>) (o1, o2) -> -OriginSortMerger.this.compare(
                DefaultPartitionDoc.newInstance(o1),
                DefaultPartitionDoc.newInstance(o2)
        ));
    }
}
