package com.huya.search.index.data.merger;

import com.huya.search.index.data.QueryResult;
import com.huya.search.index.data.SingleQueryResult;
import com.huya.search.index.meta.MetaEnum;
import org.apache.lucene.index.IndexableField;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/22.
 */
public class TimeCountMerger extends Merger {

    @Override
    public QueryResult<? extends Iterable<IndexableField>> result() {
        List<Iterable<IndexableField>> temp = new ArrayList<>();
        Map<String, Integer> tempMap = new HashMap<>();
        for (Map.Entry<String, PartDocuments> entry : getMergeGroup().entrySet()) {
            for (Iterable<IndexableField> iterable : entry.getValue().getDocumentList()) {
                final String[]  time       = new String[1];
                final Integer[] countValue = new Integer[1];
                iterable.forEach(indexableField -> {
                    if (Objects.equals(indexableField.name(), MetaEnum.TIMESTAMP)) {
                        time[0] = indexableField.stringValue();
                    }
                    else if (Objects.equals(indexableField.name(), "*")) {
                        Number number = indexableField.numericValue();
                        if (number == null) {
                            countValue[0] = Integer.parseInt(indexableField.stringValue());
                        }
                        else {
                            countValue[0] = number.intValue();
                        }
                    }
                });

                if (tempMap.containsKey(time[0])) {
                    tempMap.put(time[0], tempMap.get(time[0]) + countValue[0]);
                }
                else {
                    tempMap.put(time[0], countValue[0]);
                }
            }
        }
        takeOver();
        tempMap.forEach((key, value) -> {
            IndexableField timestampIndexableField = SingleQueryResult.createStringIndexableField("timestamp", key);
            IndexableField countIndexableField     = SingleQueryResult.createNumberIndexableField("*", value);
            temp.add(Arrays.asList(timestampIndexableField, countIndexableField));
        });
        return new QueryResult<>(temp);
    }
}
