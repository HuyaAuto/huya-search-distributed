package com.huya.search.facing.producer;

import com.huya.search.service.AbstractOrderService;

import java.util.Properties;

public abstract class ProducerService extends AbstractOrderService {

    protected abstract Properties getProducerSettings();

}
