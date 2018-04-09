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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.LockReleaseFailedException;
import org.apache.solr.common.util.IOUtils;
import org.apache.solr.store.hdfs.HdfsDirectory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/28.
 *
 * 此类不支持并发
 */
@Singleton
public class LazyHdfsFileSystem extends LazyFileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(LazyHdfsFileSystem.class);

    private static final PartitionTimeOrdering PARTITION_TIME_ORDERING = new PartitionTimeOrdering();

    private Configuration hdfsConfig;

    private MetaService metaService;

    private LockFactory lockFactory;

    @Inject
    public LazyHdfsFileSystem(@Named("Settings") Settings settings, MetaService metaService, LockFactory lockFactory) {
        super(settings);
        this.metaService = metaService;
        this.lockFactory = lockFactory;
    }

    @Override
    public void close(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        openStateSet.remove(table);
        HdfsDirectory dir = (HdfsDirectory) directoryMap.remove(table);
        if (dir != null && !dir.isClosed()) {
            try {
                dir.close();
            } catch (IOException e) {
                LOG.error("close table dir error", e);
            }
        }
    }

    @Override
    public HdfsDirectory getDirectory(ShardMetaDefine metaDefine) throws IOException {
        String table = metaDefine.getTable();
        if (openStateSet.contains(table)) {
            TimelineMetaDefine timelineMetaDefine = metaService.get(table);
            PartitionCycle first = timelineMetaDefine.getFirstPartitionCycle();
            PartitionCycle cycle = metaDefine.getPartitionCycle();
            if (first.compareTo(cycle) <= 0) {
                HdfsDirectory directory = (HdfsDirectory) directoryMap.get(metaDefine.path());
                if (directory != null) {
                    return directory;
                } else {
                    Path path = new Path(path(metaDefine.path()));
                    directory = new HdfsDirectory(path, lockFactory, hdfsConfig, HdfsDirectory.DEFAULT_BUFFER_SIZE);
                    directoryMap.put(metaDefine.path(), directory);
                    return directory;
                }
            } else {
                throw new RuntimeException("get directory earlier than meta define");
            }
        }
        throw new RuntimeException("this table meta not open");
    }

    @Override
    public List<PartitionCycle> getLastDirectoryPartitionCycles(ShardCoordinate shardCoordinate) throws IOException {
        String path = PathUtil.separator + BASE_PATH + PathUtil.separator + shardCoordinate.getTable() + PathUtil.separator + shardCoordinate.getShardId();
        FileSystem hdfs = FileSystem.get(URI.create(path), hdfsConfig);
        Path dirPath = new Path(path);
        List<PartitionCycle> partitionCycles = new ArrayList<>();

        if (hdfs.exists(dirPath)) {

            DateTime dateTime = DateTime.now();

            String thisYear = dateTime.toString(JodaUtil.YYYY_FORMATTER);
            String lastYear = dateTime.plusYears(-1).toString(JodaUtil.YYYY_FORMATTER);

            List<FileStatus> fileStatuses = Arrays.asList(hdfs.listStatus(dirPath, filterPath -> {
                String pathName = filterPath.getName();
                return pathName != null && (pathName.startsWith(thisYear) || pathName.startsWith(lastYear));
            }));

            fileStatuses = PARTITION_TIME_ORDERING.greatestOf(fileStatuses, lastPartitionNum);


            for (FileStatus fileStatus : fileStatuses) {
                partitionCycles.add(new PartitionCycle(fileStatus.getPath().getName()));
            }
        }
        return partitionCycles;
    }

    @Override
    public void lockShards(ShardCoordinate shardCoordinate) throws IOException {
        String path = PathUtil.separator + BASE_PATH + "/" + shardCoordinate.getTable() + "/" + shardCoordinate.getShardId();
        FileSystem hdfs = FileSystem.get(URI.create(path), hdfsConfig);
        Path dirPath = new Path(path);
        Path lockFile = new Path(dirPath, SHARDS_LOCK);

        FSDataOutputStream file = null;
        while (true) {
            try {
                if (!hdfs.exists(dirPath)) {
                    boolean success = hdfs.mkdirs(dirPath);
                    if (!success) {
                        throw new RuntimeException("Could not create directory: " + dirPath);
                    }
                } else {
                    // just to check for safe mode
                    hdfs.mkdirs(dirPath);
                }

                file = hdfs.create(lockFile, false);
                break;
            } catch (FileAlreadyExistsException e) {
                throw new LockObtainFailedException("Cannot obtain lock file: " + lockFile, e);
            } catch (RemoteException e) {
                if (e.getClassName().equals(
                        "org.apache.hadoop.hdfs.server.namenode.SafeModeException")) {
                    LOG.warn("The NameNode is in SafeMode - huyasearch will wait 5 seconds and try again.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        Thread.interrupted();
                    }
                    continue;
                }
                throw new LockObtainFailedException("Cannot obtain lock file: " + lockFile, e);
            } catch (IOException e) {
                throw new LockObtainFailedException("Cannot obtain lock file: " + lockFile, e);
            } finally {
                IOUtils.closeQuietly(file);
            }
        }
    }

    @Override
    public void unLockShards(ShardCoordinate shardCoordinate) throws IOException {
        String path = PathUtil.separator + BASE_PATH + PathUtil.separator + shardCoordinate.getTable() + PathUtil.separator + shardCoordinate.getShardId();
        FileSystem hdfs = FileSystem.get(URI.create(path), hdfsConfig);
        Path dirPath = new Path(path);
        Path lockFile = new Path(dirPath, SHARDS_LOCK);
        try {
            if (hdfs.exists(lockFile) && !hdfs.delete(lockFile, false)) {
                throw new LockReleaseFailedException("failed to delete: " + lockFile);
            }
        } finally {
            IOUtils.closeQuietly(hdfs);
        }
    }

    private String path(String path) {
        return PathUtil.separator + BASE_PATH + PathUtil.separator + path;
    }

    @Override
    protected void doStart() throws SearchException {
        if (hdfsConfig == null) {
            try {
                initHdfsConfig();
            } catch (IOException e) {
                throw new SearchException("init hdf config errior", e);
            }
        }
        metaService.register(this);
        LOG.info(getName() + " started");
    }

    private void initHdfsConfig() throws IOException {
        hdfsConfig = new Configuration();
        hdfsConfig.addResource(new Path(settings.get("core-site.xml")));
        hdfsConfig.addResource(new Path(settings.get("hdfs-site.xml")));
        hdfsConfig.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        hdfsConfig.set("fs.file.impl", LocalFileSystem.class.getName());
        hdfsConfig.set("fs.hdfs.impl.disable.cache", "true");
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
        LOG.info(getName() + " stoped");
    }

    @Override
    protected void doClose() throws SearchException {
        directoryMap.clear();
        hdfsConfig = null;
        LOG.info(getName() + " closed");
    }

    @Override
    public String getName() {
        return "LayzHdfsFileSystem";
    }


    static class PartitionTimeOrdering extends Ordering<FileStatus> {

        private Map<String, PartitionCycle> cycleCache = new HashMap<>();

        @Override
        public int compare(FileStatus left, FileStatus right) {
            String leftFileName  = left.getPath().getName();
            String rightFileName = right.getPath().getName();

            PartitionCycle leftCycle  = getCycle(leftFileName);
            PartitionCycle rightCycle = getCycle(rightFileName);

            return leftCycle.compareTo(rightCycle);
        }

        private PartitionCycle getCycle(String fileName) {
            PartitionCycle cycle = cycleCache.get(fileName);
            if (cycle == null) {
                cycle = new PartitionCycle(fileName);
                cycleCache.put(fileName, cycle);
            }
            return cycle;
        }
    }
}
