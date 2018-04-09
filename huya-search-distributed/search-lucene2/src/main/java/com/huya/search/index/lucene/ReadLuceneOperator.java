package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.memory.UseFreq;
import org.apache.lucene.store.Directory;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/30.
 */
public abstract class ReadLuceneOperator extends LuceneOperator implements LuceneReader, LuceneQuery {
    ReadLuceneOperator(ShardMetaDefine metaDefine, Directory directory, IndexWriterConfigFactory configFactory, LuceneOperatorType luceneOperatorType) {
        super(metaDefine, directory, configFactory, luceneOperatorType);
    }

    ReadLuceneOperator(ShardMetaDefine metaDefine, Directory directory, IndexWriterConfigFactory configFactory, UseFreq useFreq, LuceneOperatorType luceneOperatorType) {
        super(metaDefine, directory, configFactory, useFreq, luceneOperatorType);
    }
}
