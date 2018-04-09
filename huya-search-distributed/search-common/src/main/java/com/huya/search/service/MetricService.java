package com.huya.search.service;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.inject.Inject;
import com.huya.search.SearchException;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/18.
 */
public class MetricService extends AbstractLifecycleService {

    private MetricRegistry registry = new MetricRegistry();

    private ScheduledReporter reporter;

    private int period;

    private TimeUnit unit;

    @Inject
    public MetricService(ScheduledReporter reporter, int period, TimeUnit unit) {
        this.reporter = reporter;
        this.period = period;
        this.unit = unit;
    }

    public Meter getMeter(Class clazz, String todo, String type) {
        return registry.meter(MetricRegistry.name(clazz, todo, type));
    }

    @Override
    protected void doStart() throws SearchException {
        reporter.start(period, unit);
    }

    @Override
    protected void doStop() throws SearchException {
        reporter.stop();
    }

    @Override
    protected void doClose() throws SearchException {
        reporter.close();
    }

    @Override
    public String getName() {
        return "MetricService";
    }
}
