package com.huya.search.service;

import com.huya.search.settings.Settings;

public abstract class AbstractOrderService extends AbstractLifecycleService {

    private int priority;

    public AbstractOrderService(Settings settings) {
        super(settings);
    }

    public AbstractOrderService() {}

    public int priority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
