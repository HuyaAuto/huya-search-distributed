package com.huya.search.index.opeation;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.lucene.*;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public interface QueryContext extends ExecutorContext {

    /**
     * 查询最新结果
     * @param lazyLuceneOperators 懒加载的 LuceneOperators ，实现类需要保证，最近时间的 LuceneOperator 排在最前面
     * @param shardNum 一个时间分区的分片数量
     * @param es 线程池
     * @return 查询结果
     * @throws ExecutionException 执行异常
     * @throws InterruptedException 中断异常
     */
    QueryResult<? extends Iterable<IndexableField>> query(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) throws ExecutionException, InterruptedException;

    Set<Long> getCycles();

    Query getQuery();
}
