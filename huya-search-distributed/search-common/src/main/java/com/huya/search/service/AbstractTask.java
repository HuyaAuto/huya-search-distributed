package com.huya.search.service;

import com.huya.search.util.ThreadPoolSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/3.
 */
public abstract class AbstractTask<T extends TaskAttr> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTask.class);



    private volatile TaskState state = TaskState.NOT_RUNNING;

    private TaskException currentTaskException = null;

    private ReentrantLock lock = new ReentrantLock();

    public boolean isNotRunning() {
        return state == TaskState.NOT_RUNNING;
    }

    public boolean isStarting() {
        return state == TaskState.STARTING;
    }

    public boolean isStopping() {
        return state == TaskState.STOPPING;
    }

    public boolean isRunning() {
        return state == TaskState.RUNNING;
    }

    public boolean isException() {
        return state == TaskState.EXCEPTION;
    }

    public void start(T t) {
        try {
            lock.lock();
            try {
                if (isNotRunning()) {
                    state = TaskState.STARTING;
                }
                else {
                    LOG.warn("current task state is " + state + " could not to start");
                    return;
                }
            } finally {
                lock.unlock();
            }

            if (isStarting()) {

                doStart(t);

                lock.lock();
                try {
                    state = TaskState.RUNNING;
                } finally {
                    lock.unlock();
                }
            }
        } catch (TaskException e) {
            state = TaskState.EXCEPTION;
            exception(e);
        }
    }

    protected abstract void doStart(T t) throws TaskException;

    public void stop(T t) {
        lock.lock();
        try {
            if (isRunning()) {
                state = TaskState.STOPPING;
            }
            else {
                LOG.warn("current task state is " + state + " could not to stop");
                return;
            }
        } finally {
            lock.unlock();
        }

        if (isStopping()) {
            try {
                doStop(t);
            } catch (TaskException e) {
                state = TaskState.EXCEPTION;
                exception(e);
                return;
            }

            lock.lock();
            try {
                state = TaskState.NOT_RUNNING;
            } finally {
                lock.unlock();
            }
        }
    }

    protected abstract void doStop(T t) throws TaskException;

    public void exception(TaskException exception) {
        lock.lock();
        try {
            currentTaskException = exception;
            if (isStarting()) {
                doStartException();
            }
            else if (isStopping()) {
                doStopException();
            }
            else if (isRunning()) {
                doRunException();
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract void doStartException();

    protected abstract void doStopException();

    protected abstract void doRunException();

    public void clearException() {
        lock.lock();
        try {
            if (isException()) {
                doClearException();
                currentTaskException = null;
                state = TaskState.NOT_RUNNING;
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract void doClearException();

    public TaskException getCurrentTaskException() {
        return currentTaskException;
    }

    public void asyncStart(T t) {
        ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> this.start(t));
    }

    public void asyncStop(T t) {
        ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> this.stop(t));
    }

    public void asyncClearException() {
        ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(this::clearException);
    }
}
