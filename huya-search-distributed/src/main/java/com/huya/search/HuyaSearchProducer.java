package com.huya.search;

import com.huya.search.facing.producer.KafkaProducerService;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.module.search.ProducerServiceModule;
import com.huya.search.module.search.ZookeeperServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.huya.search.GlobalServicePriority.*;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/10.
 */
public class HuyaSearchProducer implements Application {

    private static final Logger LOG = LoggerFactory.getLogger(HuyaSearchProducer.class);

    private OrderServiceItem<KafkaProducerService> producerService
            = new OrderServiceItem<>(KafkaProducerService.class, KAFKA_PRODUCER_SERVICE, new ProducerServiceModule());

    private static final class HuyaSearchProducerHandle {
        private static final HuyaSearchProducer INSTANCE = new HuyaSearchProducer();
    }

    public static HuyaSearchProducer getInstance() {
        return HuyaSearchProducer.HuyaSearchProducerHandle.INSTANCE;
    }

    public static void main(String[] args) {
        try {
            HuyaSearchProducer searchProducer = HuyaSearchProducer.getInstance();
            searchProducer.start();
        } catch (Exception e) {
            LOG.error("huya-search-producer error", e);
            e.printStackTrace();
        }
    }


    @Override
    public void start() {
        ModulesBuilder mb = ModulesBuilder.getInstance();
        mb.add(producerService.getAbstractModules());
        producerService.init();
        producerService.start();
    }

    @Override
    public void close() {
        producerService.close();
    }
}
