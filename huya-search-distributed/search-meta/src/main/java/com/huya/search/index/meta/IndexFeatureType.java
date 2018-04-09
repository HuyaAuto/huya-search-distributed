package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.util.JsonUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;

public class IndexFeatureType implements JsonObjectAble {

    public static final IndexFeatureType DEFAULT_FEATURE_TYPE = newInstance(IndexFieldType.String);

    public static Analyzer TEXT_DEFAULT_ANALYZER = new SimpleAnalyzer();

    private IndexFieldType type;

    private Analyzer analyzer;

    private boolean sort;

    public static IndexFeatureType newInstance(IndexFieldType type) {
        return new IndexFeatureType(type, false, null);
    }

    public static IndexFeatureType newInstance(IndexFieldType type, boolean sort, Analyzer analyzer) {
        return new IndexFeatureType(type, sort, analyzer);
    }

    private IndexFeatureType(IndexFieldType type, boolean sort, Analyzer analyzer) {
        this.type = type;
        this.sort = sort;
        this.analyzer = analyzer == null ? TEXT_DEFAULT_ANALYZER : analyzer;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public IndexFieldType getType() {
        return type;
    }

    public boolean isSort() {
        return sort;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public ObjectNode toObject() {
        ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
        objectNode.put(MetaEnum.TYPE, type.toString());
        objectNode.put(MetaEnum.SORT, sort);
        if (analyzer != null) {
            objectNode.put(MetaEnum.ANALYZER, analyzer.getClass().getName());
        }
        return objectNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexFeatureType that = (IndexFeatureType) o;

        if (sort != that.sort) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (sort ? 1 : 0);
        return result;
    }
}
