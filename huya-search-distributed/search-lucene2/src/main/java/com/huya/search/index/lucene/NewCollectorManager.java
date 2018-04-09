package com.huya.search.index.lucene;

import org.apache.lucene.search.CollectorManager;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public class NewCollectorManager implements CollectorManager<NewDocIdsCollector, List<Integer>> {

    public static NewCollectorManager newInstance(int newNum) {
        return new NewCollectorManager(newNum);
    }

    private final int newNum;

    private NewCollectorManager(int newNum) {
        this.newNum = newNum;
    }

    @Override
    public NewDocIdsCollector newCollector() throws IOException {
        return NewDocIdsCollector.newInstance(newNum);
    }

    @Override
    public List<Integer> reduce(Collection<NewDocIdsCollector> collectors) throws IOException {
        throw new RuntimeException("no support reduce");
    }

    public int newNum() {
        return newNum;
    }


}
