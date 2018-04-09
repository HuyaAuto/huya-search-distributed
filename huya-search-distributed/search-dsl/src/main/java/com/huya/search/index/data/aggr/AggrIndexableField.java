package com.huya.search.index.data.aggr;

import com.huya.search.index.data.SingleQueryResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

import java.io.Reader;

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
