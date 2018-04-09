package com.huya.search.index.block;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.index.lucene.DistributedLazyShardLuceneService;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
@Singleton
public class DistributedIndexPartitions extends IndexPartitions {

    @Inject
    public DistributedIndexPartitions(Settings settings, TimelineMetaDefine timelineMetaDefine,
                                      ShardInfosService shardInfosService, ShardsFactory shardsFactory) {
        super(settings, timelineMetaDefine, shardInfosService, shardsFactory);
    }

    @Override
    public void start() throws SearchException {
        if (pool == null) {
            this.pool = ModulesBuilder.getInstance()
                    .createInjector().getInstance(DistributedLazyShardLuceneService.class)
                    .getOperatorPool(timelineMetaDefine);
        }
        initShardsVector();
    }
}
