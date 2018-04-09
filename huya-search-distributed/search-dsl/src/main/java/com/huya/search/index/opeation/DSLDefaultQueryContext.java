package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Range;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.data.merger.MergerFactory;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.impl.PartitionDocs;
import com.huya.search.index.dsl.function.DslType;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.lucene.*;
import com.huya.search.index.opeation.builder.DSLQueryBuilder;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public class DSLDefaultQueryContext extends AbstractDSLQueryContext {

    public static QueryContext newInstance(DSLQueryBuilder dslQueryBuilder) {
        return new DSLDefaultQueryContext(dslQueryBuilder);
    }

    DSLDefaultQueryContext(DSLQueryBuilder dslQueryBuilder) {
        super(dslQueryBuilder);
    }

    @Override
    public DslType getDslType() {
        return DslType.DEFAULT_QUERY_LIMIT;
    }

    @Override
    protected Range<Integer> eachLuceneOperatorQueryRange(int operatorNum, int shardNum) {
        LimitExpr limitExpr = getLimit();
        int from = limitExpr.from();
        int to   = limitExpr.to();
        return Range.closed(from / (operatorNum * shardNum) , to / (operatorNum * shardNum) + 1);
    }

    @Override
    protected List<Future<List<? extends Iterable<IndexableField>>>>
    getFutureDocuments(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) {
        final List<Future<List<? extends Iterable<IndexableField>>>> futures = new ArrayList<>(lazyLuceneOperators.size());
        Query          query  = getQuery();
        Set<String>    fields = getSelectFields();
        Range<Integer> range  = eachLuceneOperatorQueryRange(lazyLuceneOperators.size(), shardNum);

        lazyLuceneOperators.forEach(lazyLuceneOperator -> futures.add(
                es.submit(
                        () -> lazyLuceneOperator.lazyGet()
                        .query(query, fields, range.lowerEndpoint(), range.upperEndpoint()))
                )
        );

        return futures;
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

    /**
     * 目前简单的明细不允许进行分组，需要分组则必须带上排序字段
     * 使用 DSLSortQueryContext
     * @return 合并器
     */
    @Override
    public Merger getMerger() {
        return MergerFactory.createMerger(getLimit().size());
    }


    public static class DSLDefaultQueryContextSerializer extends Serializer<DSLDefaultQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DSLDefaultQueryContext object) {
            kryo.writeObject(output, object.dslQueryBuilder);
        }

        @Override
        public DSLDefaultQueryContext read(Kryo kryo, Input input, Class<DSLDefaultQueryContext> type) {
            return new DSLDefaultQueryContext(kryo.readObject(input, DSLQueryBuilder.class));
        }

    }


}
