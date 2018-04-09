package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.inject.ModulesBuilder;
import org.apache.lucene.store.Directory;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/11.
 */
public class LuceneOperatorFactory {

    private static final ModulesBuilder MODULES_BUILDER = ModulesBuilder.getInstance();

    private static final IndexWriterConfigFactory DISK_INDEX_WRITER_CONFIG_FACTORY =
            MODULES_BUILDER.createInjector().getInstance(IndexWriterConfigFactory.DiskIndexWriterConfigFactory.class);

    private static final IndexWriterConfigFactory RAM_INDEX_WRITER_CONFIG_FACTORY =
            MODULES_BUILDER.createInjector().getInstance(IndexWriterConfigFactory.RamIndexWriterConfigFactory.class);


    public static WriterAndReadLuceneOperator createReady(ShardMetaDefine metaDefine, Directory directory) {

        //当前拉取的分区使用内存磁盘两段式提交
        //其他的使用正常的磁盘写入
        if (metaDefine.getPartitionCycle().current()) {
            return new RamTwoPhaseLuceneOperator(metaDefine, directory,
                    DISK_INDEX_WRITER_CONFIG_FACTORY, RAM_INDEX_WRITER_CONFIG_FACTORY);
        }
        else {
            return new ReadyLuceneOperator(metaDefine, directory, DISK_INDEX_WRITER_CONFIG_FACTORY, LuceneOperatorType.HDFS);
        }
    }

}
