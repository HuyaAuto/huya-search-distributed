package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Range;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.data.merger.MergerFactory;
import com.huya.search.index.dsl.function.DslType;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.lucene.*;
import com.huya.search.index.opeation.builder.DSLSortQueryBuilder;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public class DSLSortQueryContext extends DSLDefaultQueryContext {

    public static QueryContext newInstance(DSLSortQueryBuilder dslSortQueryBuilder) {
        return new DSLSortQueryContext(dslSortQueryBuilder);
    }

    private DSLSortQueryBuilder dslSortQueryBuilder;

    DSLSortQueryContext(DSLSortQueryBuilder dslSortQueryBuilder) {
        super(dslSortQueryBuilder);
        this.dslSortQueryBuilder = dslSortQueryBuilder;
    }

    @Override
    public DslType getDslType() {
        return DslType.ORDER_QUERY_LIMIT;
    }

    @Override
    protected Range<Integer> eachLuceneOperatorQueryRange(int operatorNum, int shardNum) {
        LimitExpr limitExpr = getLimit();
        return Range.closed(0, limitExpr.to());
    }

    @Override
    protected List<Future<List<? extends Iterable<IndexableField>>>> getFutureDocuments(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) {
        final List<Future<List<? extends Iterable<IndexableField>>>> futures = new ArrayList<>(lazyLuceneOperators.size());
        Query          query  = getQuery();
        Sort           sort   = getSort();
        Set<String>    fields = getSelectFields();
        Range<Integer> range  = eachLuceneOperatorQueryRange(lazyLuceneOperators.size(), shardNum);

        lazyLuceneOperators.forEach(lazyLuceneOperator -> futures.add(es.submit(()
                -> lazyLuceneOperator.lazyGet().querySort(query, sort, fields, range.lowerEndpoint(), range.upperEndpoint()))));
        return futures;
    }

    /**
     * 返回排序合并器
     * 注意 1. 即使选择需要分组，这个过程也不会在这里进行（分组会在 Node Merger 进行），这里只进行排序
     *     2. 即使选择 from -- to 也不会在这里进行，（范围划分会在 Node Merger 进行），这里只会获取 top
     *
     * @return 排序合并器
     */
    @Override
    public Merger getMerger() {
        LimitExpr limitExpr = getLimit();
        return MergerFactory.createSortMerger(getSort(), limitExpr.top());
    }

    protected Sort getSort() {
        return dslSortQueryBuilder.getSortedItems().sortInstance();
    }

    public static class DSLSortQueryContextSerializer extends Serializer<DSLSortQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DSLSortQueryContext object) {
            kryo.writeObject(output, object.dslSortQueryBuilder);
        }

        @Override
        public DSLSortQueryContext read(Kryo kryo, Input input, Class<DSLSortQueryContext> type) {
            return new DSLSortQueryContext(kryo.readObject(input, DSLSortQueryBuilder.class));
        }

    }

}
