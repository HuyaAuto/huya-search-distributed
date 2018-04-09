package com.huya.search.service;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/3.
 */
public class TaskException extends Exception {

    public static TaskException notReadyInstance() {
        return new TaskException("task is not ready");
    }

    public static TaskException currentIsException(TaskException e) {
        return new TaskException("current state is exception", e);
    }

    public TaskException() {
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }


}
