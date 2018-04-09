package com.huya.search.index.meta;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.util.AnalyzerFactory;
import org.apache.lucene.analysis.Analyzer;

import java.util.HashMap;
import java.util.Map;

public class IndexMapBuilder {

    public static IndexMapBuilder newInstance() {
        return new IndexMapBuilder();
    }

    private IndexMapBuilder() {}

    private Map<String, IndexFeatureType> map = new HashMap<>();

    public IndexMapBuilder add(String name, IndexFieldType type, boolean sort, Analyzer analyzer) {
        map.put(name, IndexFeatureType.newInstance(type, sort, analyzer));
        return this;
    }

    public IndexMapBuilder add(ObjectNode objectNode) {
        objectNode.fields().forEachRemaining(entry -> {
            String  name = entry.getKey();
            JsonNode jsonNode = entry.getValue();
            String type = "string";

            if(jsonNode.hasNonNull(MetaEnum.TYPE)) {
                type = jsonNode.get(MetaEnum.TYPE).textValue();
            }

            boolean sort = false;

            if(jsonNode.hasNonNull(MetaEnum.SORT)) {
                sort = jsonNode.has(MetaEnum.SORT) && jsonNode.get(MetaEnum.SORT).asBoolean();
            }
            Analyzer analyzer = jsonNode.has(MetaEnum.ANALYZER) ? AnalyzerFactory.getInstance().getAnalyzer(jsonNode.get(MetaEnum.ANALYZER).textValue()) : null;
            add(name, IndexFieldType.getType(type), sort, analyzer);
        });
        return this;
    }

    public Map<String, IndexFeatureType> build() {
        return map;
    }
}
