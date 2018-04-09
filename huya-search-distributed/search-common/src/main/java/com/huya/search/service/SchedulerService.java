package com.huya.search.service;

import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/6.
 */
public interface SchedulerService {

    void addJob(SearchJob searchJob, Trigger trigger) throws SchedulerException;

    void deleteJob(SearchJob searchJob) throws SchedulerException;
}
