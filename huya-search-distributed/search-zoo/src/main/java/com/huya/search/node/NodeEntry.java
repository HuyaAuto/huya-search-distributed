package com.huya.search.node;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/9.
 */
public interface NodeEntry extends NodeBaseEntry {

    int getExpectedClusterSize();

    long getStartTimestamp();

    boolean equals(Object o);

    int hashCode();
}
