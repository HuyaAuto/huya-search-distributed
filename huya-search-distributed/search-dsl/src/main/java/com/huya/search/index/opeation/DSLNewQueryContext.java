package com.huya.search.index.opeation;

import com.google.common.collect.Range;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.data.merger.MergerFactory;
import com.huya.search.index.dsl.function.DslType;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.lucene.WriterAndReadLuceneOperator;
import com.huya.search.index.opeation.builder.DSLSortQueryBuilder;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/1.
 */
public class DSLNewQueryContext extends DSLSortQueryContext {

    public static DSLNewQueryContext newInstance(DSLSortQueryBuilder dslSortQueryBuilder) {
        return new DSLNewQueryContext(dslSortQueryBuilder);
    }

    protected DSLNewQueryContext(DSLSortQueryBuilder dslSortQueryBuilder) {
        super(dslSortQueryBuilder);
    }

    @Override
    public DslType getDslType() {
        return DslType.NEW_QUERY_LIMIT;
    }

    @Override
    protected Range<Integer> eachLuceneOperatorQueryRange(int operatorNum, int shardNum) {
        LimitExpr limitExpr = getLimit();
        int from = limitExpr.from();
        int to   = limitExpr.to();
        return Range.closed(from / shardNum , to / shardNum + 1);
    }

    @Override
    protected List<Future<List<? extends Iterable<IndexableField>>>> getFutureDocuments(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) {
        Query          query  = getQuery();
        Set<String>    fields = getSelectFields();
        Range<Integer> range  = eachLuceneOperatorQueryRange(lazyLuceneOperators.size(), shardNum);
        final Future<List<? extends Iterable<IndexableField>>> future = es.submit(() -> {
            int lower = range.lowerEndpoint();
            int upper = range.upperEndpoint();
            int limit = upper - lower;
            List<Iterable<IndexableField>> result = new ArrayList<>(limit);
            int luceneOperatorsSize = lazyLuceneOperators.size();

            if (luceneOperatorsSize == 0) {
                return Collections.emptyList();
            }

            long start = System.currentTimeMillis();

            WriterAndReadLuceneOperator first = lazyLuceneOperators.get(0).lazyGet();

            result.addAll(first.queryNew(query, fields, lower, upper));

            long end = System.currentTimeMillis();

            LOG.info("{} first query New {}  use time {}", first.tag(), result.size(), end - start);

            if (result.size() >= limit) {
                return result;
            }

            int currentResultSize = result.size();

            int pred = currentResultSize > 0 ? Math.min(limit / currentResultSize + 1, luceneOperatorsSize - 1) : luceneOperatorsSize - 1;

            List<Future<List<? extends Iterable<IndexableField>>>> futurePreds = new ArrayList<>();

            for (int i = 1; i <= pred; i++) {
                addPredsFuture(lazyLuceneOperators, es, query, fields, lower, upper, futurePreds, i);
            }

            if (getFutureResult(limit, result, futurePreds)) return result;

            futurePreds.clear();
            for (int i = pred + 1; i <= luceneOperatorsSize -1; i++) {
                addPredsFuture(lazyLuceneOperators, es, query, fields, lower, upper, futurePreds, i);
            }

            if (getFutureResult(limit, result, futurePreds)) return result;

            return result;
        });

        return Collections.singletonList(future);
    }

    private void addPredsFuture(List<LazyLuceneOperator> lazyLuceneOperators, ExecutorService es, Query query, Set<String> fields, int lower, int upper, List<Future<List<? extends Iterable<IndexableField>>>> futurePreds, int temp) {
        futurePreds.add(es.submit(() -> {
            try {
                return getPredResult(lazyLuceneOperators, query, fields, lower, upper, temp);
            } catch (Exception e) {
                LOG.error("query new error", e);
                return Collections.emptyList();
            }
        }));
    }

    private List<? extends Iterable<IndexableField>> getPredResult(List<LazyLuceneOperator> lazyLuceneOperators, Query query, Set<String> fields, int lower, int upper, int temp) throws Exception {
        long predStart = System.currentTimeMillis();
        WriterAndReadLuceneOperator luceneOperator = lazyLuceneOperators.get(temp).lazyGet();
        List<? extends Iterable<IndexableField>> predResult = luceneOperator.queryNew(query, fields, lower, upper);
        long predEnd = System.currentTimeMillis();
        LOG.info("{}  query New {}  use time {}", luceneOperator.tag(), predResult.size(), predEnd - predStart);
        return predResult;
    }

    private boolean getFutureResult(int limit, List<Iterable<IndexableField>> result, List<Future<List<? extends Iterable<IndexableField>>>> futurePreds) throws InterruptedException, java.util.concurrent.ExecutionException {
        for (Future<List<? extends Iterable<IndexableField>>> predFutureResult : futurePreds) {
            List<? extends Iterable<IndexableField>> predResult = predFutureResult.get();
            result.addAll(predResult);
            if (result.size() >= limit) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Merger getMerger() {
        return MergerFactory.createMerger(getLimit().size());
    }
}
