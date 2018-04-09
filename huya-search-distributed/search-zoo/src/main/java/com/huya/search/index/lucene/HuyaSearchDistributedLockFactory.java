package com.huya.search.index.lucene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.node.NodePath;
import com.huya.search.node.ZooKeeperOperator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.lucene.store.*;
import org.apache.solr.store.hdfs.HdfsDirectory;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/8.
 */
@Singleton
public class HuyaSearchDistributedLockFactory extends LockFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HuyaSearchDistributedLockFactory.class);

    private ZooKeeperOperator zooKeeperOperator;

    @Inject
    public HuyaSearchDistributedLockFactory(ZooKeeperOperator zooKeeperOperator) {
        this.zooKeeperOperator = zooKeeperOperator;
    }

    @Override
    public Lock obtainLock(Directory dir, String lockName) throws IOException {
        if (!(dir instanceof HdfsDirectory)) {
            throw new UnsupportedOperationException("HdfsLockFactory can only be used with HdfsDirectory subclasses, got: " + dir);
        }

        final HdfsDirectory hdfsDir = (HdfsDirectory) dir;
        String path = hdfsDir.getHdfsDirPath().toUri().getPath();
        String lockPath = NodePath.lucenePath(path);

        try {
            zooKeeperOperator.getClient().create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL).withACL(OPEN_ACL_UNSAFE).forPath(lockPath);
        } catch (Exception e) {
            throw new LockObtainFailedException("Cannot obtain lock file: " + lockPath + ", acquire is timeout", e);
        }


        return new ZookeeperLuceneLock(zooKeeperOperator.getClient(), lockPath);
    }

    private static final class ZookeeperLuceneLock extends Lock {

        private String path;

        private CuratorFramework client;

        ZookeeperLuceneLock(CuratorFramework client, String path) {
            this.client = client;
            this.path = path;
        }

        @Override
        public void close() throws IOException {
            try {
                client.delete().forPath(path);
                LOG.info("release lock {} ", path);
            } catch (Exception e) {
                LOG.error("failed to release: " + path, e);
            }
        }

        @Override
        public void ensureValid() throws IOException {
            // no idea how to implement this on zookeeper
        }

        @Override
        public String toString() {
            return "ZookeeperLuceneLock(lockFile=" + path + ")";
        }
    }
}
