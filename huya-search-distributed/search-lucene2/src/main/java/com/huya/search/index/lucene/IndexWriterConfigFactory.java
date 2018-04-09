package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.settings.Settings;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/5.
 *
 * <pre>
 * 场景：为多个字段指定分词。
 * {@link org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper}
 *
 * @Note:ZhangXueJun
 * </pre>
 */
public interface IndexWriterConfigFactory {

    IndexWriterConfig createConfig();

    @Singleton
    class DiskIndexWriterConfigFactory implements IndexWriterConfigFactory {

        private Settings settings;

        @Inject
        public DiskIndexWriterConfigFactory(@Named("Settings") Settings settings) {
            this.settings = settings;
        }

        @Override
        public IndexWriterConfig createConfig() {
            Class clazz = settings.getAsClass("analyzer", Analyzer.class);
            try {
                Analyzer analyzer = (Analyzer) clazz.newInstance();
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
                indexWriterConfig.setMaxBufferedDocs(settings.getAsInt("maxBufferedDocs", 500000));
                indexWriterConfig.setRAMBufferSizeMB(settings.getAsInt("ramBufferedSizeMB", 2000));
                LogByteSizeMergePolicy logByteSizeMergePolicy = new LogByteSizeMergePolicy();
                logByteSizeMergePolicy.setMaxMergeMB(settings.getAsInt("maxMergeMB", 100));
                logByteSizeMergePolicy.setMinMergeMB(settings.getAsInt("minMergeMB", 10));
                indexWriterConfig.setMergePolicy(logByteSizeMergePolicy);
                indexWriterConfig.setUseCompoundFile(settings.getAsBoolean("useCompoundFile", true));
                indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                return indexWriterConfig;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    @Singleton
    class RamIndexWriterConfigFactory implements IndexWriterConfigFactory {

        private Settings settings;

        @Inject
        public RamIndexWriterConfigFactory(@Named("Settings") Settings settings) {
            this.settings = settings;
        }

        @Override
        public IndexWriterConfig createConfig() {
            Class clazz = settings.getAsClass("analyzer", Analyzer.class);
            try {
                Analyzer analyzer = (Analyzer) clazz.newInstance();
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
                indexWriterConfig.setMaxBufferedDocs(settings.getAsInt("ramMaxBufferedDocs", 50000));
                indexWriterConfig.setUseCompoundFile(settings.getAsBoolean("useCompoundFile", true));
                indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                return indexWriterConfig;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
