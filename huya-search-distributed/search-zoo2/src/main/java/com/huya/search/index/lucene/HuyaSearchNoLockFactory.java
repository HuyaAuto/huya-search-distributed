package com.huya.search.index.lucene;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/11.
 */
public class HuyaSearchNoLockFactory extends LockFactory {
    @Override
    public Lock obtainLock(Directory dir, String lockName) throws IOException {
        return new Lock() {
            @Override
            public void close() throws IOException {
                //do nothing
            }

            @Override
            public void ensureValid() throws IOException {
                //do nothing
            }
        };
    }
}
