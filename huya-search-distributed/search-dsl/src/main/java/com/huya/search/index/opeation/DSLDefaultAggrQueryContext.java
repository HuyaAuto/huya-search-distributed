package com.huya.search.index.opeation;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.huya.search.index.data.*;
import com.huya.search.index.data.impl.PartitionDocs;
import com.huya.search.index.data.merger.*;
import com.huya.search.index.dsl.function.DslType;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.lucene.WriterAndReadLuceneOperator;
import com.huya.search.index.meta.MetaEnum;
import com.huya.search.index.opeation.builder.DSLQueryBuilder;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DSLDefaultAggrQueryContext extends DSLDefaultQueryContext {

    protected DSLDefaultAggrQueryContext(DSLQueryBuilder dslQueryBuilder) {
        super(dslQueryBuilder);
    }

    @Override
    public DslType getDslType() {
        return DslType.AGGR_QUERY;
    }

    @Override
    protected Range<Integer> eachLuceneOperatorQueryRange(int operatorNum, int shardNum) {
        return Range.all();
    }

    @Override
    protected List<Future<List<? extends Iterable<IndexableField>>>> getFutureDocuments(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) {
        final List<Future<List<? extends Iterable<IndexableField>>>> futures = new ArrayList<>(lazyLuceneOperators.size());
        Query        query        = getQuery();

        final int num = getGroupGrainNum();

        PartitionRange times = getTimes(num);


        //todo 目前只支持 count ，计划添加 max min sum
        lazyLuceneOperators.forEach(lazyLuceneOperator -> futures.add(es.submit(() -> {
            try {
                WriterAndReadLuceneOperator luceneOperator = lazyLuceneOperator.lazyGet();
                List<Range<Long>> rangeList = times.getHasIntersectionRangeList(luceneOperator.getPartitionCycle().getRange());
                if (rangeList.size() == 0) {
                    return Collections.emptyList();
                }
                else {
                    return luceneOperator.queryCount(query, rangeList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        })));

        return futures;
    }

    private PartitionRange getTimes(int splitNum) {
        long low = getWhereCondition().getLowCycle();
        long up  = getWhereCondition().getUpCycle();
        long range = (up - low) / splitNum;
        List<Range<Long>> rangeList = new ArrayList<>();
        for (int i = 0; i < splitNum; i++) {
            long current = low + range;
            rangeList.add(Range.range(low, BoundType.CLOSED, current, BoundType.OPEN));
            low = current;
        }
        return new PartitionRange(rangeList);
    }

    private int getGroupGrainNum() {
        GroupByItems groupByItems = getGroupByItems();
        if (groupByItems == null) {
            return 1;
        }
        else {
            String temp = groupByItems.groupFields().get(0).substring(MetaEnum.GRAIN.length());
            return Integer.parseInt(temp);
        }
    }

    @Override
    protected QueryResult<? extends Iterable<IndexableField>> mergerDocuments(List<Future<List<? extends Iterable<IndexableField>>>> futures) throws ExecutionException, InterruptedException {
        Merger merger = getMerger();
        int tag = 0;
        for (Future<List<? extends Iterable<IndexableField>>> future : futures) {
            merger.add(PartitionDocs.newInstance(String.valueOf(tag ++), future.get()));
        }
        return merger.result();
    }

//    @Override
//    public QueryResult<? extends Iterable<IndexableField>> query(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) throws ExecutionException, InterruptedException {
//        SelectItems selectItems = getSelectItems();
//        if (selectItems.onlyCount()) {
//            return queryAllNoGroupOnlyCount(lazyLuceneOperators, es);
//        }
//        else {
//            return super.query(lazyLuceneOperators, shardNum, es);
//        }
//    }
//
//    @Override
//    protected QueryResult<? extends Iterable<IndexableField>> queryAll(List<Future<ShardDocIds>> futureList, ExecutorService es) throws ExecutionException, InterruptedException {
//        GroupByItems groupByItems = getGroupByItems();
//
//        //没有 limit 的查询不支持分组
//        assert groupByItems == null;
//
//        return queryAllNoGroupNoOnlyCount(futureList, es);
//    }

//    private QueryResult<? extends Iterable<IndexableField>> queryAllWithGroup(List<Future<ShardDocIds>> futureList, ExecutorService es, GroupByItems groupByItems) throws ExecutionException, InterruptedException {
//        Merger merger = getMerger();
//        DefaultAggrFunUnitSet aggrFunSet = getAggrFunSet();
//
//        Set<String> fields = new HashSet<>();
//        fields.addAll(aggrFunSet.aggrFields());
//        fields.addAll(groupByItems.groupFields());
//
//        final List<Future<PartitionDocs>> futureDocsList = getAllDocs(futureList, es, fields);
//
//        for (Future<PartitionDocs> future : futureDocsList) {
//            merger.add(future.get());
//        }
//
//        return merger.result();
//    }

//
//    /**
//     * 获取没有分组的聚合查询，并且聚合查询并非全是 COOUNT 计算的结果
//     * @param futureList 文档列表
//     * @param es 线程池
//     * @return 查询结果
//     * @throws ExecutionException
//     * @throws InterruptedException
//     */
//    protected QueryResult<? extends Iterable<IndexableField>> queryAllNoGroupNoOnlyCount(List<Future<ShardDocIds>> futureList, ExecutorService es) throws ExecutionException, InterruptedException {
//        AggrMerger merger = (AggrMerger) getMerger();
//
//        //聚合需要查询的字段
//        Set<String> aggrFields = merger.aggrFields();
//
//        final List<Future<PartitionDocs>> futureDocsList = getAllDocs(futureList, es, aggrFields);
//
//        for (Future<PartitionDocs> future : futureDocsList) {
//            merger.add(future.get());
//        }
//        return merger.result();
//    }
//
//    private List<Future<PartitionDocs>> getAllDocs(List<Future<ShardDocIds>> futureList, ExecutorService es, Set<String> fields) throws ExecutionException, InterruptedException {
//        final List<Future<PartitionDocs>> futureDocsList = new ArrayList<>();
//
//        for (Future<ShardDocIds> future : futureList) {
//            ShardDocIds docIds = future.get();
//            if (!docIds.isEmpty()) {
//                futureDocsList.add(es.submit(() -> docIds.syncDocs(fields)));
//            }
//        }
//        return futureDocsList;
//    }
//
//    /**
//     * 获取没有分组的聚合查询，并且聚合查询全是 COUNT 计算的结果
//     * @param lazyLuceneOperators luceneOperator 操作集合
//     * @param es 线程池
//     * @return 查询结果
//     * @throws ExecutionException
//     * @throws InterruptedException
//     */
//    protected QueryResult<? extends Iterable<IndexableField>> queryAllNoGroupOnlyCount(List<LazyLuceneOperator> lazyLuceneOperators, ExecutorService es) throws ExecutionException, InterruptedException {
//        List<Future<Long>> futures = new ArrayList<>(lazyLuceneOperators.size());
//        for (LazyLuceneOperator lazyLuceneOperator : lazyLuceneOperators) {
//            futures.add(es.submit(() -> lazyLuceneOperator.lazyGet().queryCount(this)));
//        }
//        long num = 0;
//        for (Future<Long> future : futures) {
//            num += future.get();
//        }
//        Set<String> countFields = getSelectItems().getCountFields();
//        return SingleQueryResult.SingleLineNum(countFields, num);
//    }
//
//    @Override
//    protected QueryResult<? extends Iterable<IndexableField>> queryPart(List<Future<ShardDocIds>> futureList, ExecutorService es) throws ExecutionException, InterruptedException {
//        GroupByItems groupByItems = getGroupByItems();
//        if (groupByItems == null) {
//            return queryPartNoGroup(futureList, es);
//        }
//        else {
//            return queryPartWithGroup(futureList, es, groupByItems);
//        }
//    }
//
//    private QueryResult<? extends Iterable<IndexableField>> queryPartNoGroup(List<Future<ShardDocIds>> futureList, ExecutorService es) throws ExecutionException, InterruptedException {
//        SelectItems selectItems = getSelectItems();
//        if (selectItems.onlyCount()) {
//            return queryPartNoGroupOnlyCount(futureList, es);
//        }
//        else {
//            return queryPartNoGroupNoOnlyCount(futureList, es);
//        }
//    }
//
//    private QueryResult<? extends Iterable<IndexableField>> queryPartNoGroupOnlyCount(List<Future<ShardDocIds>> futureList, ExecutorService es) throws ExecutionException, InterruptedException {
//        long num = 0;
//        LimitExpr limitExpr = getLimit();
//        int top = limitExpr.top();
//        for (Future<ShardDocIds> future : futureList) {
//            ShardDocIds docIds = future.get();
//            num += docIds.size();
//            if (num >= top) {
//                num = top;
//                break;
//            }
//        }
//        num -= limitExpr.from();
//        Set<String> countFields = getSelectItems().getCountFields();
//        return SingleQueryResult.SingleLineNum(countFields, num);
//    }
//
//    private QueryResult<? extends Iterable<IndexableField>> queryPartNoGroupNoOnlyCount(List<Future<ShardDocIds>> futureList, ExecutorService es) throws ExecutionException, InterruptedException {
//        AggrMerger merger = (AggrMerger) getMerger();
//
//        //聚合需要查询的字段
//        Set<String> aggrFields = merger.aggrFields();
//
//        final List<Future<PartitionDocs>> futureDocsList = getPartDocs(futureList, es, aggrFields);
//
//        for (Future<PartitionDocs> future : futureDocsList) {
//            merger.add(future.get());
//        }
//        return merger.result();
//    }
//
//    private QueryResult<? extends Iterable<IndexableField>> queryPartWithGroup(List<Future<ShardDocIds>> futureList, ExecutorService es, GroupByItems groupByItems) throws ExecutionException, InterruptedException {
//        Merger merger = getMerger();
//        DefaultAggrFunUnitSet aggrFunSet = getAggrFunSet();
//
//        Set<String> fields = new HashSet<>();
//        fields.addAll(aggrFunSet.aggrFields());
//        fields.addAll(groupByItems.groupFields());
//
//        final List<Future<PartitionDocs>> futureDocsList = getPartDocs(futureList, es, fields);
//
//        for (Future<PartitionDocs> future : futureDocsList) {
//            merger.add(future.get());
//        }
//
//        return merger.result();
//    }

    @Override
    public Merger getMerger() {
//        GroupByItems groupByItems = getGroupByItems();
//        if (groupByItems == null) {
//            return new AggrMerger(getAggrFunSet());
//        }
//        else {
//            return new AggrGroupMerger(groupByItems.groupFields(), getMetaDefine(), getAggrFunSet());
//        }
        return new Merger();
//        DefaultAggrFunSetBuilder builder = DefaultAggrFunSetBuilder.newInstance();
//        builder.add("*", "*", AggrFun.SUM, IndexFieldType.Long);
//        return new AggrMerger(builder.build());
    }

    public DefaultAggrFunUnitSet getAggrFunSet() {
        return getSelectItems().getAggrFunSet();
    }

}
