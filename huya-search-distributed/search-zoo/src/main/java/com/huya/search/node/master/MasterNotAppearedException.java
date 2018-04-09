package com.huya.search.node.master;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/23.
 */
public class MasterNotAppearedException extends RuntimeException {

    public MasterNotAppearedException() {
        super("master not appeared");
    }

}
