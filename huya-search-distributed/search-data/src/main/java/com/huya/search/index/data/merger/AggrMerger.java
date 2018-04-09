package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class AggrMerger extends Merger {

    private DefaultAggrFunUnitSet defaultAggrFunUnitSet;

    public AggrMerger(DefaultAggrFunUnitSet defaultAggrFunUnitSet) {
        super();
        this.defaultAggrFunUnitSet = defaultAggrFunUnitSet;
    }

    @Override
    public QueryResult<? extends Iterable<IndexableField>> result() {
        Map<String, PartDocuments> map = getMergeGroup();

        for (Map.Entry<String, PartDocuments> entry : map.entrySet()) {
            PartDocuments partDocuments = entry.getValue();
            List<? extends Iterable<IndexableField>> docs = partDocuments.getDocumentList();
            for (Iterable<IndexableField> iterable : docs) {
                for (IndexableField anIterable : iterable) {
                    defaultAggrFunUnitSet.add(anIterable);
                }
            }
        }


        return defaultAggrFunUnitSet.result();
    }

    public Set<String> aggrFields() {
        return defaultAggrFunUnitSet.aggrFields();
    }
}
