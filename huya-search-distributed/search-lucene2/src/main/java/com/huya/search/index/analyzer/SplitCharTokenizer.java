package com.huya.search.index.analyzer;

import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/25.
 */
public class SplitCharTokenizer extends CharTokenizer {

    public static SplitCharTokenizer newInstance(char c) {
        return new SplitCharTokenizer(c);
    }

    private char c;

    private SplitCharTokenizer(char c) {
        this.c = c;
    }

    @Override
    protected boolean isTokenChar(int c) {
        return !(c == this.c);
    }
}
