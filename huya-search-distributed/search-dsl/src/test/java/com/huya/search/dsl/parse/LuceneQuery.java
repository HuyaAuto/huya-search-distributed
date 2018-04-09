package com.huya.search.dsl.parse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.junit.Test;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public class LuceneQuery {

    @Test
    public void runTest() throws ParseException {
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(DoublePoint.newRangeQuery("test", 0.1, 0.2), BooleanClause.Occur.MUST);
        BooleanQuery query = bqb.build();
        System.out.println(query);
        QueryParser queryParser = new QueryParser(null, new StandardAnalyzer());
        BooleanQuery booleanClauses = (BooleanQuery) queryParser.parse(query.toString());

        System.out.println(booleanClauses);

    }
}
