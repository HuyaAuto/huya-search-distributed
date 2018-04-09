package com.huya.search.service;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/10.
 */
public interface PriorityLifecycleService extends LifecycleService {

    /**
     * the higher the value returned, the higher the priority
     * @return
     */
    int startPriority();

    /**
     * the higher the value returned, the higher the priority
     * @return
     */
    int stopPriority();
}
