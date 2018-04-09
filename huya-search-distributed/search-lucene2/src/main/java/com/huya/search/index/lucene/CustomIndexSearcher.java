package com.huya.search.index.lucene;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Range;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.ThreadInterruptedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/25.
 */
public class CustomIndexSearcher extends IndexSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(CustomIndexSearcher.class);

    // 预计你的查询结果大部分时间最多占到总量的 10%
    private final static float DEFAULT_ESTIMATED_PERCENTAGE = 0.1f;

    private final static int DEFAULT_ESTIMATED_LEAF = 2;

    private List<LeafReaderContext> leafReaderContexts;

    private ExecutorService es;

    public CustomIndexSearcher(IndexReader r, ExecutorService executor) {
        super(r, executor);
        this.leafReaderContexts = r.getContext().leaves();
        this.es = executor;
    }

    public List<Integer> search(Query query, MakeUpCollectorManager collectorManager) throws IOException {
        LOG.info("查询search.generic.query:" + query.toString());

        final int numDoc = getIndexReader().numDocs();

        int numHits = collectorManager.makeUp();

        final MakeUpWeight weight = new MakeUpWeight(createNormalizedWeight(query, false), numHits);

        int needQueryLeafNum, estLeafSlices;

        estLeafSlices = numDoc > 0
                ? (int) Math.ceil(numHits / (numDoc * DEFAULT_ESTIMATED_PERCENTAGE))
                : DEFAULT_ESTIMATED_LEAF;

        needQueryLeafNum = Math.min(estLeafSlices, leafReaderContexts.size());


        final List<Future<DocIdsCollector>> collectorFutures = new ArrayList<>(needQueryLeafNum);

        for (int i = 0; i < needQueryLeafNum; i++) {
            final LeafReaderContext leafReaderContext = leafReaderContexts.get(i);
            DocIdsCollector collector = collectorManager.newCollector();
            collectorFutures.add(es.submit(() -> {
                search(Collections.singletonList(leafReaderContext), weight, collector);
                return collector;
            }));
        }

        List<Integer> temp = new ArrayList<>();

        if (tryToReturnMakeUp(temp, collectorFutures, numHits)) {
            return temp;
        }

        for (int i = needQueryLeafNum; i < leafReaderContexts.size(); i++) {
            final LeafReaderContext leafReaderContext = leafReaderContexts.get(i);
            DocIdsCollector collector = collectorManager.newCollector();
            collectorFutures.add(es.submit(() -> {
                search(Collections.singletonList(leafReaderContext), weight, collector);
                return collector;
            }));
        }

        tryToReturnMakeUp(temp, collectorFutures, numHits);

        return temp;
    }

    private boolean tryToReturnMakeUp(List<Integer> temp, List<Future<DocIdsCollector>> collectorFutures, int numHits) {
        for (Future<DocIdsCollector> collectorFuture : collectorFutures) {
            try {
                temp.addAll(collectorFuture.get().getDocIdList());
                if (temp.size() >= numHits) {
                    return true;
                }
            } catch (InterruptedException e) {
                throw new ThreadInterruptedException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public List<Integer> search(Query query, NewCollectorManager collectorManager) throws IOException {
        LOG.info("查询search.new.query:" + query.toString());
        if (leafReaderContexts.size() == 0) return Collections.emptyList();

        final int newNum = collectorManager.newNum();
        final int leafSize = leafReaderContexts.size();

        MinMaxPriorityQueue<Integer> queue;
        final Weight weight = createNormalizedWeight(query, false);

        final NewDocIdsCollector collector = collectorManager.newCollector();
        final LeafReaderContext leafReaderContext = leafReaderContexts.get(leafSize - 1);
        search(Collections.singletonList(leafReaderContext), weight, collector);
        queue = collector.getQueue();
        int size = queue.size();

        if (size < newNum) {
            int predictionNum = size > 0 ? newNum / size  + 1 : leafSize - 1;
            int lastPoint = -1;
            List<LeafReaderContext> list = new ArrayList<>();
            for (int i = leafSize - 2; i>= 0; i--) {
                list.add(leafReaderContexts.get(i));
                if (list.size() == predictionNum) {
                    lastPoint = i - 1;
                    break;
                }
            }
            search(list, weight, collector);

            size = queue.size();

            if (size < newNum) {
                list.clear();
                for (int i = lastPoint; i>=0 ; i--) {
                    list.add(leafReaderContexts.get(i));
                }
                search(list, weight, collector);
            }
        }

        List<Integer> temp = new ArrayList<>();

        while (!queue.isEmpty()) {
            temp.add(queue.pollFirst());
        }
        return temp;
    }

    public List<? extends Iterable<IndexableField>> groupByTimestampSearch(Query query, List<Range<Long>> rangeList) throws IOException {
        LOG.info("查询search.grain.query:" + query.toString());
        List<ConcurrentCountCollector> concurrentCountCollectorList = new ArrayList<>();

        for (Range<Long> range : rangeList) {
            concurrentCountCollectorList.add(new ConcurrentCountCollector(query, range));
        }

        List<Future> futures = new ArrayList<>();

        for (LeafReaderContext leafReaderContext1 : leafReaderContexts) {
            for (ConcurrentCountCollector collector : concurrentCountCollectorList) {
                final Weight weight = createNormalizedWeight(collector.getQuery(), false);
                final LeafReaderContext leafReaderContext = leafReaderContext1;
                futures.add(ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GRAIN).submit(() -> {
                    try {
                        search(Collections.singletonList(leafReaderContext), weight, collector);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
            }
        }

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        List<Iterable<IndexableField>> result = new ArrayList<>();

        for (ConcurrentCountCollector collector : concurrentCountCollectorList) {
            result.add(collector.getResult());
        }
        return result;
    }
}