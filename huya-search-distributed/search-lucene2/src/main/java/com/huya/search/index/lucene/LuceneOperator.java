package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.memory.UseFreq;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/10.
 */
public abstract class LuceneOperator extends BaseLuceneOperator {

    static final Logger LOG = LoggerFactory.getLogger(ReadyLuceneOperator.class);

    ShardMetaDefine metaDefine;

    Directory directory;

    IndexWriterConfigFactory configFactory;

    private LuceneOperatorType luceneOperatorType;

    LuceneOperator(ShardMetaDefine metaDefine, Directory directory, IndexWriterConfigFactory configFactory, LuceneOperatorType luceneOperatorType) {
        this.metaDefine = metaDefine;
        this.directory = directory;
        this.configFactory = configFactory;
        this.luceneOperatorType = luceneOperatorType;
    }

    LuceneOperator(ShardMetaDefine metaDefine, Directory directory, IndexWriterConfigFactory configFactory, UseFreq useFreq, LuceneOperatorType luceneOperatorType) {
        super(useFreq);
        this.metaDefine = metaDefine;
        this.directory = directory;
        this.configFactory = configFactory;
        this.luceneOperatorType = luceneOperatorType;
    }

    public abstract ReentrantReadWriteLock getLock();

    @Override
    public String tag() {
        return luceneOperatorType + " " + super.tag();
    }


    @Override
    public ShardMetaDefine getShardMetaDefine() {
        return metaDefine;
    }


    //通过元数据可以唯一确定一个 LuceneOperator
    //故 equals 与 hashCode 只使用 ShardMetaDefine 进行进行计算
    @Override
    public boolean luceneOperatorEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LuceneOperator that = (LuceneOperator) o;
        return Objects.equals(getShardMetaDefine(), that.getShardMetaDefine());
    }

    @Override
    public int luceneOperatorHashCode() {
        return Objects.hash(getShardMetaDefine());
    }
}
