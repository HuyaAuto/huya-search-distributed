package com.huya.search.util;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public class OnceBoolean {

    public static OnceBoolean newInstance(boolean flag) {
        return new OnceBoolean(flag);
    }

    private final boolean flag;
    private boolean once = true;

    private OnceBoolean(boolean flag) {
        this.flag = flag;
    }

    public void addFlag(boolean flag) {
        if (this.flag != flag) {
            once = false;
        }
    }

    public boolean getOnce() {
        return once;
    }

}
