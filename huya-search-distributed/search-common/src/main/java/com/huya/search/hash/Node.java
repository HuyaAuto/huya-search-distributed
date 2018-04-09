package com.huya.search.hash;

import java.util.Collection;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/12.
 */
public interface Node<A extends AssignObject> {

    /**
     * virtual node get hash value
     * @param i virtual node id
     * @return hash value
     */
    int hash(int i);

    void add(int id, A assignObject);

    void addAll(int id, Collection<A> handOverSet);

    void remove(int id, A assignObject);

    void removeAll(int id, Collection<A> handOverSet);

    Set<A> getAllAssignObject();

    Set<A> getAssignObject(int id);

}
