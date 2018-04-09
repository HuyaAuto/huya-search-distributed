package com.huya.search.index.data;

import org.apache.lucene.analysis.Analyzer;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/8.
 */
public class SearchDataItem {

    public static SearchDataItem newInstance(String key, Object value) {
        return new SearchDataItem(key, value);
    }

    private String key;

    private Object value;

    /**
     * 分词器:默认null
     */
    private Analyzer analyzer;

    private SearchDataItem(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public SearchDataItem(String key, Object value, Analyzer analyzer) {
        this.key = key;
        this.value = value;
        this.analyzer = analyzer;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return this.value;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    @Override
    public String toString() {
        return "SearchDataItem{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
