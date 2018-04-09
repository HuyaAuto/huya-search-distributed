package com.huya.search.index.analyzer;

import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/25.
 */
public class SplitCharsTokenizer extends CharTokenizer {

    public static SplitCharsTokenizer newInstance(String chars) {
        return new SplitCharsTokenizer(chars);
    }

    private String chars;

    private SplitCharsTokenizer(String chars) {
        this.chars = chars;
    }

    @Override
    protected boolean isTokenChar(int c) {
        return chars.indexOf(c) < 0;
    }
}
