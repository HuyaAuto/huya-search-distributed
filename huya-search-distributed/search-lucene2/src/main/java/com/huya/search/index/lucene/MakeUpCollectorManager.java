package com.huya.search.index.lucene;

import org.apache.lucene.search.CollectorManager;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/25.
 */
public class MakeUpCollectorManager implements CollectorManager<DocIdsCollector, List<Integer>> {

    public static MakeUpCollectorManager newInstance(int makeUpNum) {
        return new MakeUpCollectorManager(makeUpNum);
    }

    private int makeUpNum;

    private MakeUpCollectorManager(int makeUpNum) {
        this.makeUpNum = makeUpNum;
    }

    @Override
    public DocIdsCollector newCollector() {
        return DocIdsCollector.newInstance();
    }

    @Override
    public List<Integer> reduce(Collection<DocIdsCollector> collectors) {
        throw new RuntimeException("no support reduce");
    }

    public int makeUp() {
        return makeUpNum;
    }
}
