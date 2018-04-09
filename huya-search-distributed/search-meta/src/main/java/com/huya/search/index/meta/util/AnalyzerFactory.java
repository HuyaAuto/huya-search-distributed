package com.huya.search.index.meta.util;

import com.huya.search.util.InstanceClassUtil;
import org.apache.lucene.analysis.Analyzer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/25.
 */
public class AnalyzerFactory {

    private static final class AnalyzerFactoryHandle {
        private static final AnalyzerFactory INSTANCE = new AnalyzerFactory();
    }

    public static AnalyzerFactory getInstance() {
        return AnalyzerFactoryHandle.INSTANCE;
    }

    private Map<String, Analyzer> analyzerMap = new ConcurrentHashMap<>();

    private AnalyzerFactory() {}

    public Analyzer getAnalyzer(String name) {
        Analyzer analyzer = analyzerMap.get(name);
        if (analyzer == null) {
            try {
                analyzer = InstanceClassUtil.getInstance(name);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            analyzerMap.put(name, analyzer);
        }
        return analyzer;
    }
}
