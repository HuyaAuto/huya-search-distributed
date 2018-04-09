package com.huya.search.tasks;

import com.huya.search.index.DistributedEngine;
import com.huya.search.service.AbstractLifecycleService;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
public abstract class MasterTasksService  extends AbstractLifecycleService {

    public abstract DistributedEngine getDistributedEngine();

    public abstract void taskStart(String table);

    public abstract void taskShutdown(String table);

    public abstract void tasksStart();

    public abstract void tasksShutdown();
}
