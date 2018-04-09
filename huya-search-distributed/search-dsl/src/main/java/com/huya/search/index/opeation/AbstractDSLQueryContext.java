package com.huya.search.index.opeation;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.data.QueryResult;
import com.huya.search.index.dsl.function.DslType;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.limit.LimitExpr;
import com.huya.search.index.dsl.select.SelectItems;
import com.huya.search.index.dsl.where.WhereCondition;
import com.huya.search.index.lucene.LazyLuceneOperator;
import com.huya.search.index.meta.CorrespondTable;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.opeation.builder.DSLQueryBuilder;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public abstract class AbstractDSLQueryContext implements QueryContext, CorrespondTable {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractDSLQueryContext.class);

    protected DSLQueryBuilder dslQueryBuilder;

    protected AbstractDSLQueryContext(DSLQueryBuilder dslQueryBuilder) {
        this.dslQueryBuilder = dslQueryBuilder;
    }

    /**
     * 获取查询类型
     * @return 查询类型
     */
    public abstract DslType getDslType();

    /**
     * 获取一个 Shard 多个时间分区共同计算出的查询结果
     * @param lazyLuceneOperators 懒加载的 LuceneOperators ，实现类需要保证，最近时间排在最前面
     * @param shardNum 一个时间分区的分片数量
     * @param es 线程池
     * @return 查询结果
     * @throws ExecutionException 执行错误
     * @throws InterruptedException 中断错误
     */
    @Override
    public QueryResult<? extends Iterable<IndexableField>> query(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es) throws ExecutionException, InterruptedException {
        return mergerDocuments(getFutureDocuments(lazyLuceneOperators, shardNum, es));
    }

    /**
     * 获取每个 LuceneOperator 查询分区指定的查询范围
     * @param operatorNum LuceneOperator 数 =  shardNum * 时间周期
     * @param shardNum 一个时间分区内本节点命中的分片数
     * @return 每个 LuceneOperator 查询分区指定的查询范围
     */
    protected abstract Range<Integer> eachLuceneOperatorQueryRange(int operatorNum, int shardNum);

    /**
     * 通过懒加载的 LuceneOperator 列表，获取将要得到的各分区查询结果
     * @param lazyLuceneOperators 懒加载的 LuceneOperator 列表
     * @param shardNum 本节点命中的分片数（一个时间分区中的分片数）
     * @param es 线程池
     * @return 将要得到的各分区查询结果
     */
    protected abstract List<Future<List<? extends Iterable<IndexableField>>>> getFutureDocuments(List<LazyLuceneOperator> lazyLuceneOperators, int shardNum, ExecutorService es);

    /**
     * 合并文档，返回查询结果
     * @param futures 将获取多个分区的查询文档列表
     * @return 合并后的查询结果
     * @throws ExecutionException 执行错误
     * @throws InterruptedException 中断错误
     */
    protected abstract QueryResult<? extends Iterable<IndexableField>> mergerDocuments(List<Future<List<? extends Iterable<IndexableField>>>> futures) throws ExecutionException, InterruptedException;

    /**
     * 获取分区之间的合并器，
     * 用于合并不同分区查询到的数据
     * @return 查询合并器
     */
    protected abstract Merger getMerger();

    /**
     * 获取所有的列字段名称（不包括函数内的列）
     * @return 列名称
     */
    protected Set<String> getSelectFields() {
        return Sets.newHashSet(dslQueryBuilder.getSelectItems().getColumnFields());
    }

    /**
     * 获取 SelectItems 实体
     * @return Query 中的 Select 部分
     */
    protected SelectItems getSelectItems() {
        return dslQueryBuilder.getSelectItems();
    }

    /**
     * 获取 GroupByItems 实体
     * @return Query 中的 Group By 部分
     */
    protected GroupByItems getGroupByItems() {
        return dslQueryBuilder.getGroupByItems();
    }

    /**
     * 获取 WhereCondition 实体
     * @return Query 中 WhereCondition 部分
     */
    protected WhereCondition getWhereCondition() {
        return dslQueryBuilder.getWhereCondition();
    }


    /**
     * 获取命中的时间周期（即是命中的分区，集合中的每个值代表这个分区的第一毫秒）
     * @return Query 中的命中的时间周期
     */
    @Override
    public Set<Long> getCycles() {
        return dslQueryBuilder.getWhereCondition().getCycleList();
    }

    /**
     * 获取查询的表名
     * @return Query 中指定的表名
     */
    @Override
    public String getTable() {
        return dslQueryBuilder.getWhereCondition().getTable();
    }

    /**
     * 获取 LimitExpr 实体
     * @return Query 中的 Limit 部分
     */
    protected LimitExpr getLimit() {
        return dslQueryBuilder.getLimitExpr();
    }

    /**
     * 获取对应的 Lucene 的查询实体
     * @return Query 中表达的 Lucene Query
     */
    @Override
    public Query getQuery() {
        IntactMetaDefine intactMetaDefine = getSelectItems().getMetaDefine();
        return dslQueryBuilder.getWhereCondition().getQuery(intactMetaDefine);
    }

    /**
     * 获取查询对应的元数据
     * @return Query 对应的元数据
     */
    public MetaDefine getMetaDefine() {
        return dslQueryBuilder.getSelectItems().getMetaDefine();
    }

}
