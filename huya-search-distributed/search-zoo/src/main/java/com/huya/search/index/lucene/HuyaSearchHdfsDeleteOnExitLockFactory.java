package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.lucene.store.*;
import org.apache.solr.common.util.IOUtils;
import org.apache.solr.store.hdfs.HdfsDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/21.
 */
@Singleton
public class HuyaSearchHdfsDeleteOnExitLockFactory extends LockFactory {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    public HuyaSearchHdfsDeleteOnExitLockFactory() {}

    @Override
    public Lock obtainLock(Directory dir, String lockName) throws IOException {
        if (!(dir instanceof HdfsDirectory)) {
            throw new UnsupportedOperationException("HdfsLockFactory can only be used with HdfsDirectory subclasses, got: " + dir);
        }
        final HdfsDirectory hdfsDir = (HdfsDirectory) dir;
        final Configuration conf = hdfsDir.getConfiguration();
        final Path lockPath = hdfsDir.getHdfsDirPath();
        final Path lockFile = new Path(lockPath, lockName);
        FSDataOutputStream file = null;
        final FileSystem fs = FileSystem.get(lockPath.toUri(), conf);
        while (true) {
            try {
                if (!fs.exists(lockPath)) {
                    boolean success = fs.mkdirs(lockPath);
                    if (!success) {
                        throw new RuntimeException("Could not create directory: " + lockPath);
                    }
                } else {
                    // just to check for safe mode
                    fs.mkdirs(lockPath);
                }

                if (fs.deleteOnExit(lockFile)) {
                    fs.delete(lockFile, false);
                    throw new FileAlreadyExistsException("set deleteOnExit for: " + lockPath + " but it is exist");
                }
                else {
                    file = fs.create(lockFile, false);
                }

                break;
            } catch (FileAlreadyExistsException e) {
                throw new LockObtainFailedException("Cannot obtain lock file: " + lockFile, e);
            } catch (RemoteException e) {
                if (e.getClassName().equals(
                        "org.apache.hadoop.hdfs.server.namenode.SafeModeException")) {
                    log.warn("The NameNode is in SafeMode - Solr will wait 5 seconds and try again.");
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

        return new HuyaSearchHdfsDeleteOnExitLockFactory.HdfsLock(conf, lockFile);
    }

    private static final class HdfsLock extends Lock {

        private final Configuration conf;
        private final Path lockFile;
        private volatile boolean closed;

        HdfsLock(Configuration conf, Path lockFile) {
            this.conf = conf;
            this.lockFile = lockFile;
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            final FileSystem fs = FileSystem.get(lockFile.toUri(), conf);
            try {
                if (fs.exists(lockFile) && !fs.delete(lockFile, false)) {
                    throw new LockReleaseFailedException("failed to delete: " + lockFile);
                }
            } finally {
                IOUtils.closeQuietly(fs);
            }
        }

        @Override
        public void ensureValid() throws IOException {
            // no idea how to implement this on HDFS
        }

        @Override
        public String toString() {
            return "HdfsLock(lockFile=" + lockFile + ")";
        }
    }
}
