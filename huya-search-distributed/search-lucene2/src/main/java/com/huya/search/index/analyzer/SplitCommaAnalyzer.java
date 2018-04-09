package com.huya.search.index.analyzer;

import org.apache.lucene.analysis.Analyzer;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/5.
 */
public class SplitCommaAnalyzer extends Analyzer {

    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        return new Analyzer.TokenStreamComponents(SplitCharsTokenizer.newInstance(", "));
    }

}