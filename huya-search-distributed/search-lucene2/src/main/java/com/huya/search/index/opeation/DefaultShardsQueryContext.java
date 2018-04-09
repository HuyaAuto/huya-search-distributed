package com.huya.search.index.opeation;

import com.google.common.collect.Lists;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.lucene.LazyLuceneOperator;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public class DefaultShardsQueryContext extends ShardsQueryContext {

    public static ShardsQueryContext newInstance(int shardId, int shardNum, QueryContext queryContext) {
        return new DefaultShardsQueryContext(shardId, shardNum, queryContext);
    }

    private int shardId;

    private int shardNum;

    private QueryContext queryContext;

    private DefaultShardsQueryContext(int shardId, int shardNum, QueryContext queryContext) {
        this.shardId = shardId;
        this.shardNum = shardNum;
        this.queryContext = queryContext;
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> query(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) throws ExecutionException, InterruptedException {
        return queryContext.query(lazyLuceneOperators, shardNum, es);
    }

    @Override
    public Set<Long> getCycles() {
        return queryContext.getCycles();
    }

    @Override
    public Query getQuery() {
        return queryContext.getQuery();
    }

    @Override
    public String getTable() {
        return queryContext.getTable();
    }

    /**
     * 保证了最近降序排列
     * @return 命中的时间
     */
    @Override
    public Collection<Long> getUnixTimes() {
        List<Long> temp = Lists.newArrayList(getCycles());
        temp.sort((o1, o2) -> -Long.compare(o1, o2));
        return temp;
    }

    @Override
    public int getShardId() {
        return shardId;
    }

    @Override
    public int getShardNum() {
        return shardNum;
    }
}
