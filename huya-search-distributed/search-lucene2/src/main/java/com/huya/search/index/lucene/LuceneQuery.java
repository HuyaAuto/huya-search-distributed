package com.huya.search.index.lucene;

import com.google.common.collect.Range;
import com.huya.search.index.data.merger.AggrFunUnitSet;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/1.
 */
public interface LuceneQuery {

    boolean exist(long offset) throws IOException;

    long maxOffset() throws IOException;

    /**
     * 默认查询，按照条件提取有限的明细数据，不涉及排序，最新等规则
     * @param query lucene query
     * @param fields 指定获取的域
     * @param from 查询起点（基于一个分片的）
     * @param to 查询终点（基于一个分片的）
     * @return 文档列表
     * @throws IOException io 异常
     */
    List<? extends Iterable<IndexableField>> query(Query query, Set<String> fields, int from, int to) throws IOException;

    /**
     * 最新查询，按照条件提取有限的、最新写入的明细数据
     * @param query lucene query
     * @param fields 指定获取的域
     * @param from 查询起点（基于一个分片的）
     * @param to 查询终点（基于一个分片的）
     * @return 文档列表
     */
    List<? extends Iterable<IndexableField>> queryNew(Query query, Set<String> fields, int from, int to) throws IOException;

    /**
     * 排序查询，按照条件与排序规则提取有限的明细数据
     * @param query lucene query
     * @param sort 排序规则
     * @param fields 指定获取的域
     * @param from 查询起点（基于一个分片的）
     * @param to 查询终点（基于一个分片的）
     * @return 文档列表
     * @throws ExecutionException 执行错误
     * @throws InterruptedException 中断错误
     */
    List<? extends Iterable<IndexableField>> querySort(Query query, Sort sort, Set<String> fields, int from, int to) throws IOException;

    /**
     * 聚合查询，按照条件计算聚合结果
     * @param query lucene query
     * @param aggrFunUnitSet 聚合函数
     * @return 文档列表
     */
    List<? extends Iterable<IndexableField>> queryAggr(Query query, AggrFunUnitSet aggrFunUnitSet) throws IOException;

    /**
     * Count 聚合查询
     * @param query lucene query
     * @return 文档列表（内容已经不是索引文档，何事聚合后的统计结果）
     * @throws IOException
     */
    List<? extends Iterable<IndexableField>> queryCount(Query query) throws IOException;

    /**
     * Count 聚合查询
     * @param query lucene query
     * @param rangeList 时间范围
     * @return 文档列表（内容已经不是索引文档，何事聚合后的统计结果）
     * @throws IOException
     */
    List<? extends Iterable<IndexableField>> queryCount(Query query, List<Range<Long>> rangeList) throws IOException;


}
