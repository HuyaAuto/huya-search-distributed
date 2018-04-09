package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.impl.PartitionDoc;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排序合并器，需要注意的是内部使用的是多路归并排序，你必须保证加入的 PartitionDocs 内部已经是按照 Sort 排序好的
 * 如果 PartitionDocs 是从 Lucene query 出的结果自然是不需要担心，但是继承此类后用于其他环境就需要注意这一点，
 * 例如 OriginSortMerger
 * Created by zhangyiqun1@yy.com on 2017/9/6.
 */
public class SortMerger extends Merger {

    private Sort sort;

    private int from = 0;

    public SortMerger(Sort sort, int top) {
        super(top);
        this.sort = sort;
    }

    public SortMerger(Sort sort, int from, int to) {
        super(to + 1);
        this.sort = sort;
        this.from = from;
    }

    public SortMerger(Sort sort) {
        super();
        this.sort = sort;
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> result() {
        freeze();
        List<Iterable<IndexableField>> result = new ArrayList<>();
        Map<String, PartDocument> temp = new HashMap<>();
        Map<String, PartDocuments> mergeGroup = getMergeGroup();
        for (Map.Entry<String, PartDocuments> entry : mergeGroup.entrySet()) {
            PartDocuments partDoc = entry.getValue();
            if (!partDoc.isTakeOver()) {
                temp.put(partDoc.tag(), partDoc.getCurrentDoc());
                partDoc.move();
            }
        }

        int num = 0;
        while (true) {
            PartDocument top = null;
            for (Map.Entry<String, PartDocument> entry : temp.entrySet()) {
                PartDocument partDoc = entry.getValue();
                if (compare(top, partDoc) < 0) {
                    top = partDoc;
                }
            }
            if (top != null) {
                if (num >= from) {
                    result.add(top.getDocument());
                }
                num ++;
                if (num >= getTop()) {
                    break;
                }

                PartDocument next = top.next();
                if (next != null) {
                    temp.put(next.getTag(), next);
                }
                else {
                    temp.remove(top.getTag());
                }
            }
            else {
                break;
            }
        }
        if (!isMergePart() || getSumSize() <= getTop()) {
            takeOver();
        }
        return new QueryResult<>(result);
    }

    protected int compare(PartitionDoc a, PartitionDoc b) {
        if (a == null && b == null) return 0;
        else if (a == null) return -1;
        else if (b == null) return 1;
        else {

            SortField[] fields = sort.getSort();
            for (SortField field : fields) {
                int reverse = field.getReverse() ? 1 : -1;
                String fieldName = field.getField();
                SortField.Type type = field.getType();
                IndexableField aField = a.getField(fieldName), bField = b.getField(fieldName);

                int c = IndexableFieldCompare.compare(type, aField, bField, reverse);

                if (c != 0) return c;
            }
            return 0;
        }
    }

    public Sort getSort() {
        return sort;
    }
}
