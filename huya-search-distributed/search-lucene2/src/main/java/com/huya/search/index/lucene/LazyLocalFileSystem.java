package com.huya.search.index.lucene;

import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.SearchException;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.opeation.ShardCoordinate;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.settings.Settings;
import com.huya.search.util.JodaUtil;
import com.huya.search.util.PathUtil;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/30.
 */
@Singleton
public class LazyLocalFileSystem extends LazyFileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(LazyLocalFileSystem.class);

    private static final LastModifiedOrdering LAST_MODIFIED_ORDERING = new LastModifiedOrdering();

    private MetaService metaService;

    private Map<String, Directory> directoryMap = new HashMap<>();

    @Inject
    public LazyLocalFileSystem(@Named("Settings") Settings settings, MetaService metaService) {
        super(settings);
        this.metaService = metaService;
    }

    @Override
    public void close(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        openStateSet.remove(table);
        NIOFSDirectory dir = (NIOFSDirectory) directoryMap.remove(table);
        if (dir != null) {
            try {
                dir.close();
            } catch (IOException e) {
                LOG.error("close table dir error", e);
            }
        }
    }

    @Override
    public Directory getDirectory(ShardMetaDefine metaDefine) throws IOException {
        String table = metaDefine.getTable();
        if (openStateSet.contains(table)) {
            TimelineMetaDefine timelineMetaDefine = metaService.get(table);
            PartitionCycle first = timelineMetaDefine.getFirstPartitionCycle();
            PartitionCycle cycle = metaDefine.getPartitionCycle();
            if (first.compareTo(cycle) <= 0) {
                Directory directory = directoryMap.get(metaDefine.path());
                if (directory != null) {
                    return directory;
                } else {
                    Path path = Paths.get(path(metaDefine.path()));
                    directory = new NIOFSDirectory(path);
                    directoryMap.put(metaDefine.path(), directory);
                    return directory;
                }
            } else {
                throw new IOException("get directory earlier than meta define");
            }
        }
        throw new RuntimeException("this table meta not open");
    }

    //todo 实现本地磁盘的接口   （暂时没必要）

    @Override
    public void lockShards(ShardCoordinate shardCoordinate) throws IOException {

    }

    @Override
    public void unLockShards(ShardCoordinate shardCoordinate) throws IOException {

    }

    @Override
    public List<PartitionCycle> getLastDirectoryPartitionCycles(ShardCoordinate shardCoordinate) throws IOException {
        String path = PathUtil.separator + BASE_PATH + PathUtil.separator + shardCoordinate.getTable() + PathUtil.separator + shardCoordinate.getShardId();
        File dir = new File(path);
        List<PartitionCycle> partitionCycles = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            DateTime dateTime = DateTime.now();

            String thisYear = dateTime.toString(JodaUtil.YYYY_FORMATTER);
            String lastYear = dateTime.plusYears(-1).toString(JodaUtil.YYYY_FORMATTER);

            File[] partitionsDir = dir.listFiles(pathname -> {
                if (pathname != null) {
                    String pathName = pathname.getName();
                    return pathName.startsWith(thisYear) || pathName.startsWith(lastYear);
                } else {
                    return false;
                }
            });


            if (partitionsDir != null) {
                List<File> temp = Arrays.asList(partitionsDir);
                temp = LAST_MODIFIED_ORDERING.greatestOf(temp, lastPartitionNum);
                for (File file : temp) {
                    partitionCycles.add(new PartitionCycle(file.getName()));
                }
            }
        }

        return partitionCycles;
    }

    private String path(String path) {
        return "/home/hadoop/" + BASE_PATH + PathUtil.separator + path;
    }

    @Override
    protected void doStart() throws SearchException {

        LOG.info(getName() + " started");
    }

    @Override
    protected void doStop() throws SearchException {
        LOG.info(getName() + " stoped");
    }

    @Override
    protected void doClose() throws SearchException {
        directoryMap.clear();
        LOG.info(getName() + " closed");
    }

    @Override
    public String getName() {
        return "LayzlocalFileSystem";
    }

    static class LastModifiedOrdering extends Ordering<File> {

        @Override
        public int compare(File left, File right) {
            return Long.compare(left.lastModified(), right.lastModified());
        }
    }

}
