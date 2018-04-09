package com.huya.search.index.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * 动态分词器
 *
 * @author ZhangXueJun
 * @date 2018年03月27日
 */
public class DynamicAnalyzer extends Analyzer {

//    private static final Map<String, CharTokenizer> charTokenizerMap = Maps.newHashMap();

    private String app;
    private String module;
    private String type;
    private String chars;

    public static final DynamicAnalyzer getInstance(String app, String module, String type, String chars) {
        return new DynamicAnalyzer(app, module, type, chars);
    }

    private DynamicAnalyzer(String app, String module, String type, String chars) {
        this.app = app;
        this.module = module;
        this.type = type;
        this.chars = chars;
    }

    public String getApp() {
        return app;
    }

    public String getModule() {
        return module;
    }

    public String getType() {
        return type;
    }

    public String getChars() {
        return chars;
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        CharTokenizer charTokenizer = SplitCharsTokenizer.newInstance(chars);
        return new Analyzer.TokenStreamComponents(charTokenizer);
    }

    @Override
    public String toString() {
        return "DynamicAnalyzer{" +
                "app='" + app + '\'' +
                ", module='" + module + '\'' +
                ", type='" + type + '\'' +
                ", chars='" + chars + '\'' +
                '}';
    }
}
