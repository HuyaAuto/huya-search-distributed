package com.huya.search.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/28.
 */
@Singleton
public class JobSchedulerService extends AbstractLifecycleService implements SchedulerService {

    private Scheduler scheduler;

    @Inject
    public JobSchedulerService() throws SchedulerException {
        scheduler = new StdSchedulerFactory().getScheduler();
    }

    @Override
    public void addJob(SearchJob searchJob, Trigger trigger) throws SchedulerException {
        JobDetail job = newJob(searchJob.getJobClass())
                .withIdentity(searchJob.getJobKey())
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    @Override
    public void deleteJob(SearchJob searchJob) throws SchedulerException {
        scheduler.deleteJob(searchJob.getJobKey());
    }


    @Override
    protected synchronized void doStart() throws SearchException {
        try {
            if (scheduler.isStarted()) {
                scheduler.resumeAll();
            }
            else {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new SearchException("start scheduler error", e);
        }
    }

    @Override
    protected synchronized void doStop() throws SearchException {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new SearchException("stop scheduler error", e);
        }
    }

    @Override
    protected synchronized void doClose() throws SearchException {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new SearchException("close scheduler error", e);
        }
    }

    @Override
    public String getName() {
        return "JobSchedulerService";
    }
}
