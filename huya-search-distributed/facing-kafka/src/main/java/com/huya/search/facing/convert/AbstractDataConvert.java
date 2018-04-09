package com.huya.search.facing.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/1.
 */
public abstract class AbstractDataConvert<T, P, R> implements DataConvert<T, P, R> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractDataConvert.class);

    /**
     * 表名
     */
    protected String table;

    /**
     * 分片标识
     */
    protected int shardId = -1;

    private long offset1;

    private Thread monitorThread = null;

    @Override
    public R convert(int id, long offset, T t, P p) throws IgnorableConvertException {
        try {
            if (monitorThread == null) {
                this.monitorThread = new Thread(() -> {
                    while (true) {
                        logger.info(table +"[" + shardId + "]" +"消费kafka偏移：" + offset1);
                        try {
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }, table + "cumulative_count");
                monitorThread.start();
            }
            this.offset1 = offset;
            R r = realConvert(id, offset, t, p);
            return r;
        } catch (Exception e) {
            throw new IgnorableConvertException(e.getMessage(), e);
        }
    }

    protected abstract R realConvert(int id, long offset, T t, P p) throws IgnorableConvertException;

    @Override
    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public void setShardId(int shardId) {
        this.shardId = shardId;
    }
}
