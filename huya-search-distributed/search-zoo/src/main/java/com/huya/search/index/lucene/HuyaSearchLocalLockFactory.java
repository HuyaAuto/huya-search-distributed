package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.node.ZooKeeperOperator;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/8.
 */
@Singleton
public class HuyaSearchLocalLockFactory extends LockFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ZooKeeperOperator zooKeeperOperator;

    @Inject
    public HuyaSearchLocalLockFactory(ZooKeeperOperator zooKeeperOperator) {
        this.zooKeeperOperator = zooKeeperOperator;
    }

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

                file = fs.create(lockFile, false);
                file.writeBytes(zooKeeperOperator.getServiceUrl());
                file.flush();
                break;
            } catch (FileAlreadyExistsException e) {
                BufferedReader bf = null;
                try {
                    bf = new BufferedReader(new InputStreamReader(fs.open(lockFile)));
                    String serviceUrl = bf.readLine();
                    if (Objects.equals(serviceUrl, zooKeeperOperator.getServiceUrl())) {
                        //如果锁里的内容与打开他的节点 serviceUrl 一致，便获取锁
                        break;
                    } else {
                        throw new LockObtainFailedException("Cannot obtain lock file: " + lockFile + ", file content is " + serviceUrl + ", I am " + zooKeeperOperator.getServiceUrl(), e);
                    }
                } finally {
                    IOUtils.closeQuietly(bf);
                }
            } catch (RemoteException e) {
                if (e.getClassName().equals(
                        "org.apache.hadoop.hdfs.server.namenode.SafeModeException")) {
                    LOG.warn("The NameNode is in SafeMode - Solr will wait 5 seconds and try again.");
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

        return new HdfsLock(conf, lockFile);
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
