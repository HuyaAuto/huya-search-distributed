package com.huya.search.facing.producer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/10.
 */
@Singleton
public class ProducerCommandImpl extends ProducerCommand {

    private Producer producer;

    @Inject
    public ProducerCommandImpl(Producer producer) {
        this.producer = producer;
    }

    @Override
    protected void doProduce(String[] args) {
        String scriptName = args[0];
        String topic      = args[1];
        long   num        = Long.parseLong(args[2]);
        producer.producerData(topic, scriptName, num);
    }


}
