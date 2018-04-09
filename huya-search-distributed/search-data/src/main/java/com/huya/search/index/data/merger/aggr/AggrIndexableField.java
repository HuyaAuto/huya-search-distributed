package com.huya.search.index.data.merger.aggr;

import com.huya.search.index.data.SingleQueryResult;
import org.apache.lucene.index.IndexableField;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/20.
 */
public class AggrIndexableField {

    public static IndexableField numberIndexableField(String field, Number number) {
        return SingleQueryResult.createNumberIndexableField(field, number);
    }

    public static IndexableField strIndexableField(String field, String str) {
        return SingleQueryResult.createStringIndexableField(field, str);
    }
}
