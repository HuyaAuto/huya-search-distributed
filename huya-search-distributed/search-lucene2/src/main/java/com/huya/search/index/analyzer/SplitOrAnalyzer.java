package com.huya.search.index.analyzer;

import org.apache.lucene.analysis.Analyzer;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/25.
 */
public class SplitOrAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        return new TokenStreamComponents(SplitCharsTokenizer.newInstance("| "));
    }

}
