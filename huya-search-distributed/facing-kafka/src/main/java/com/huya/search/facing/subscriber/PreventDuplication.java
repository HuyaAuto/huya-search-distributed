package com.huya.search.facing.subscriber;

import com.huya.search.index.block.ShardsOperatorException;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/17.
 */
public interface PreventDuplication {

    ConsumerRecords<byte[], byte[]> preventDuplicationSeekAnaPoll() throws ShardsOperatorException;

}
