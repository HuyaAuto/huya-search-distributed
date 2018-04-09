package com.huya.search;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.service.AbstractOrderService;
import com.huya.search.service.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderServiceItem<T extends AbstractOrderService> {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceItem.class);

    private Class<T> clazz;

    private T aos;

    private GlobalServicePriority priority;

    private List<Module> abstractModules = new ArrayList<>();

    private List<LifecycleListener> listeners    = new ArrayList<>();

    OrderServiceItem(Class<T> clazz, GlobalServicePriority priority, AbstractModule... module) {
        this.clazz = clazz;
        this.priority = priority;
        Collections.addAll(abstractModules, module);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void init() {
        ModulesBuilder modulesBuilder = ModulesBuilder.getInstance();
        aos = modulesBuilder.createInjector().getInstance(clazz);
        aos.setPriority(priority.getPriority());
        for (LifecycleListener listener : listeners) {
            aos.addLifecycleListener(listener);
        }
        LOG.info("init " + aos.getName() + " finish");
    }

    public List<Module> getAbstractModules() {
        return abstractModules;
    }

    public void add(LifecycleListener lifecycleListener) {
        listeners.add(lifecycleListener);
    }

    public boolean start() {
        try {
            aos.start();
            return true;
        } catch (RuntimeException e) {
            LOG.error("start " + aos.getName() + " error", e);
            return false;
        }
    }

    public boolean close() {
        try {
            aos.close();
            return true;
        } catch (RuntimeException e) {
            LOG.error("close " + aos.getName() + " error", e);
            return false;
        }
    }

    public T getAos() {
        return aos;
    }
}
