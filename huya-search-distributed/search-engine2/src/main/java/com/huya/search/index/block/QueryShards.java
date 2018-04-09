package com.huya.search.index.block;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.opeation.ShardsQueryContext;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/10.
 */
public interface QueryShards {

    QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext queryContext, int shardNum, ExecutorService executorService) throws ExecutionException, InterruptedException;


    class DefaultQueryShards implements QueryShards {

        public static DefaultQueryShards newInstance(List<LazyLuceneOperator> lazyLuceneOperators) {
            return new DefaultQueryShards(lazyLuceneOperators);
        }

        private List<LazyLuceneOperator> lazyLuceneOperators;

        private DefaultQueryShards(List<LazyLuceneOperator> lazyLuceneOperators) {
            this.lazyLuceneOperators = lazyLuceneOperators;
        }


        @Override
        public QueryResult<? extends Iterable<IndexableField>> query(ShardsQueryContext queryContext, int shardNum, ExecutorService executorService) throws ExecutionException, InterruptedException {
            return queryContext.query(lazyLuceneOperators, shardNum, executorService);
        }
    }

}
