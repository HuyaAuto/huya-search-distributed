package com.huya.search.facing.subscriber;

import com.huya.search.index.meta.CorrespondTopic;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/3.
 */
public interface ShardConsumer extends CorrespondTopic {

    boolean isRun();

    int shardId();

    void start();

    void startFromEnd();

    void stop();

    void close();

}
