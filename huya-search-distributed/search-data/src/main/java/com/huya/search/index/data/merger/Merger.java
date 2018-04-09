package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.impl.DefaultPartitionDoc;
import com.huya.search.index.data.impl.PartitionDocs;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/6.
 */
public class Merger {

    private int top;

    private int sumSize;

    private boolean mergePart;

    private boolean freeze;

    private boolean takeOver;

    private Map<String, PartDocuments> mergeGroup = new HashMap<>();


    public Merger(int top) {
        this.top = top;
        this.mergePart = true;
    }

    public Merger() {
        this.top = Integer.MAX_VALUE;
        this.mergePart = false;
    }

    public void add(PartitionDocs partitionDocs) {
        if (!freeze) {
            mergeGroup.put(partitionDocs.tag(), new PartDocuments(partitionDocs));
            sumSize += partitionDocs.size();
        }
    }

    public QueryResult<? extends Iterable<IndexableField>> result() {
        freeze();
        return mergePart && sumSize > top ? partResult(top) : allResult();
    }

    private QueryResult<? extends Iterable<IndexableField>> allResult() {
        List<Iterable<IndexableField>> temp = new ArrayList<>();
        for (Map.Entry<String, PartDocuments> entry : mergeGroup.entrySet()) {
            temp.addAll(entry.getValue().getDocumentList());
        }
        takeOver();
        return new QueryResult<>(temp);
    }

    private QueryResult<? extends Iterable<IndexableField>> partResult(int top) {
        List<Iterable<IndexableField>> temp = new ArrayList<>();
        int num = 0;
        for (Map.Entry<String, PartDocuments> entry : mergeGroup.entrySet()) {
            PartDocuments partDoc = entry.getValue();
            List<? extends Iterable<IndexableField>> documents = partDoc.getDocumentList();
            int size = documents.size();
            if (top > num + size) {
                temp.addAll(documents);
                num += size;
                partDoc.point = size;
            }
            else {
                temp.addAll(documents.subList(0, top - num - 1));
                partDoc.point = top - num;
                break;
            }
        }
        return new QueryResult<>(temp);
    }

    public boolean isEnough() {
        return sumSize > top;
    }

    public int getTop() {
        return top;
    }

    public boolean isMergePart() {
        return mergePart;
    }

    public Map<String, PartDocuments> getMergeGroup() {
        return mergeGroup;
    }

    protected void freeze() {
        freeze = true;
    }

    public int getSumSize() {
        return sumSize;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void takeOver() {
        takeOver = true;
    }

    public boolean isTakeOver() {
        return takeOver;
    }

    protected class PartDocuments extends PartitionDocs {

        private int point = 0;

        public PartDocuments(PartitionDocs partitionDocs) {
            super(partitionDocs.tag(), partitionDocs.getDocumentList());
        }

        public boolean isTakeOver() {
            return point == size();
        }

        public void move() {
            point++;
        }

        public PartDocument getCurrentDoc() {
            return new PartDocument(getDocumentList().get(point), this);
        }
    }

    protected class PartDocument extends DefaultPartitionDoc {

        private PartDocuments documents;

        PartDocument(Iterable<IndexableField> document, PartDocuments documents) {
            super(document);
            this.documents = documents;
        }

        public PartDocument next() {
            if (!documents.isTakeOver()) {
                PartDocument partDocument = documents.getCurrentDoc();
                documents.move();
                return partDocument;
            }
            else {
                return null;
            }
        }

        public String getTag() {
            return documents.tag();
        }

    }

    public Map<String, Integer> getPointMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, PartDocuments> entry : getMergeGroup().entrySet()) {
            map.put(entry.getKey(), entry.getValue().point);
        }
        return map;
    }
}