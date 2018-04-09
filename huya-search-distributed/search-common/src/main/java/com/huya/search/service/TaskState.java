package com.huya.search.service;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/28.
 */
public enum TaskState {

    NOT_RUNNING,    //未运行
    STARTING,       //启动中
    RUNNING,        //运行中
    STOPPING,       //关闭中
    EXCEPTION       //异常中
    ;


    @Override
    public String toString() {
        return this.name();
    }
}
