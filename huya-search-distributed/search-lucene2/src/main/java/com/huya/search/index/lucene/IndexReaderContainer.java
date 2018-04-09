package com.huya.search.index.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;


public interface IndexReaderContainer extends LuceneQuery {

    /**
     * 打开读容器
     * @param writer 写者
     * @throws IOException io 异常
     */
    void open(IndexWriter writer) throws IOException;

    /**
     * 打开读容器
     * @param directory 索引目录
     * @throws IOException io 异常
     */
    void open(Directory directory) throws IOException;

    /**
     * 查询文档 ID，注入到收集器中
     * @param query 查询
     * @param collector 收集器
     */
    void search(Query query, Collector collector);

    /**
     * 获取排序前 n 的文档
     * @param query 查询
     * @param n top N
     * @param sort 排序方式
     * @return 前 n 的文档
     */
    TopFieldDocs search(Query query, int n, Sort sort);

    /**
     * 刷新
     * @throws IOException io 异常
     */
    void refresh() throws IOException;

    /**
     * 标签
     * @return 标签值
     */
    String tag();

    /**
     * 是否已经被打开
     * @return 是否打开
     */
    boolean isOpen();

    /**
     * 关闭读容器
     * @throws IOException io 异常
     */
    void close() throws IOException;

    /**
     * 获取最大偏移
     * @return 最大偏移值
     * @throws IOException io 异常
     */
    long maxOffset() throws IOException;

    /**
     *
     * @return
     */
    long count(Query query) throws IOException;
}
