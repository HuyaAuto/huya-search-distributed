package com.huya.search.service;

import org.quartz.Job;
import org.quartz.JobKey;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/28.
 */
public abstract class SearchJob {

    abstract Class<? extends Job> getJobClass();

    abstract JobKey getJobKey();

}
