package com.huya.search.index.lucene;

import com.huya.search.index.meta.MetaFollower;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.ShardCoordinate;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.service.AbstractOrderService;
import com.huya.search.settings.Settings;
import com.huya.search.util.PathUtil;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/25.
 */
public abstract class LazyFileSystem extends AbstractOrderService implements MetaFollower {

    public static final String BASE_PATH = "huya-search";

    public static final String SHARDS_LOCK = "shards.lock";

    Map<String, Directory> directoryMap = new HashMap<>();

    Set<String> openStateSet = new HashSet<>();

    int lastPartitionNum;

    LazyFileSystem(Settings settings) {
        super(settings);
        lastPartitionNum = settings.getAsInt("lastPartitionNum", 24);
    }

    @Override
    public void open(TimelineMetaDefine metaDefine) {
        openStateSet.add(metaDefine.getTable());
    }

    public String fileName(String path) {
        String temp[] = path.split(PathUtil.separator);
        return temp[temp.length - 1];
    }

    public abstract Directory getDirectory(ShardMetaDefine metaDefine) throws IOException;

    public abstract List<PartitionCycle> getLastDirectoryPartitionCycles(ShardCoordinate shardCoordinate) throws IOException;

    public abstract void lockShards(ShardCoordinate shardCoordinate) throws IOException;

    public abstract void unLockShards(ShardCoordinate shardCoordinate) throws IOException;
}
