package com.huya.search.index.opeation;

import com.huya.search.hash.AssignObject;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/10.
 */
public abstract class PullContext implements AssignObject, ExecutorContext {

    protected final static String PULL_METHOD_PREFIX = "pull.method.";
    
    public abstract String getTable();

    public abstract int getShardId();

    public abstract PullMethod getMethod();

    public enum PullMethod {
        FOLLOW,
        END;

        @Override
        public String toString() {
            return this.name();
        }
    }

}
