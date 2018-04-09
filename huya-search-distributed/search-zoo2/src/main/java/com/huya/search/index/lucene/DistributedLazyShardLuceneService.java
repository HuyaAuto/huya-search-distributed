package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.memory.MemoryCore;
import com.huya.search.node.ZooKeeperOperator;
import com.huya.search.settings.Settings;


/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
@Singleton
public class DistributedLazyShardLuceneService extends LazyShardLuceneService {

    private ZooKeeperOperator zo;

    @Inject
    public DistributedLazyShardLuceneService(@Named("Settings") Settings settings, LazyFileSystem lazyFileSystem,
                                             @Named("LuceneOperator") MemoryCore<WriterAndReadLuceneOperator> memoryCore,
                                             ZooKeeperOperator zo) {
        super(settings, lazyFileSystem, memoryCore);
        this.zo = zo;
    }

    //todo 暂时不做任何回调

    @Override
    protected void notifyApply(ShardMetaDefine shardMetaDefine) {
    }

    @Override
    protected void notifyFree(ShardMetaDefine shardMetaDefine) {
    }
}
