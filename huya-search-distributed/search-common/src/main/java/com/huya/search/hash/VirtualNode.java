package com.huya.search.hash;

import java.util.Collection;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/12.
 */
public abstract class VirtualNode<A extends AssignObject> implements AssignObject {

    public abstract int getVirtualId();

    public abstract Node getNode();

    public abstract void add(A assignObject);

    public abstract void remove(A assignObject);

    public abstract void removeAll(Collection<A> handOverSet);

    public abstract void addAll(Collection<A> handOverSet);

    public abstract Set<A> getAllAssignObject();

    public abstract Set<A> getThisAssignObject();
}
