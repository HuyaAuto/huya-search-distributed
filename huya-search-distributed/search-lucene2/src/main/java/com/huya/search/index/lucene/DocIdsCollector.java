package com.huya.search.index.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public class DocIdsCollector extends SimpleCollector {

    private int docBase;

    private List<Integer> docIdList;

    public static DocIdsCollector newInstance() {
        return new DocIdsCollector();
    }

    public static DocIdsCollector newInstance(int capacity) {
        return new DocIdsCollector(capacity);
    }

    private DocIdsCollector() {
        docIdList = new ArrayList<>();
    }

    private DocIdsCollector(int capacity) {
        docIdList = new ArrayList<>(capacity);
    }

    @Override
    public void collect(int doc) {
        docIdList.add(doc + docBase);
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) {
        this.docBase = context.docBase;
    }

    public List<Integer> getDocIdList() {
        return docIdList;
    }

    public int size() {
        return docIdList.size();
    }
}
