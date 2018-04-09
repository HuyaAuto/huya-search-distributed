package com.huya.search.index.lucene;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public class NewDocIdsCollector extends SimpleCollector {

    public static NewDocIdsCollector newInstance(int newNum) {
        return new NewDocIdsCollector(newNum);
    }

    private int docBase;

    private final int newNum;

    private MinMaxPriorityQueue<Integer> queue;

    private NewDocIdsCollector(int newNum) {
        this.newNum = newNum;
        queue = MinMaxPriorityQueue
                .orderedBy(Ordering.natural().reverse()).maximumSize(newNum).create();
    }

    @Override
    public void collect(int doc) throws IOException {
        this.queue.add(doc + docBase);
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) {
        this.docBase = context.docBase;
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    public int pollFirst() {
        return queue.pollFirst();
    }

    public MinMaxPriorityQueue<Integer> getQueue() {
        return queue;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
