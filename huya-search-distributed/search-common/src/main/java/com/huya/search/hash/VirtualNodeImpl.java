package com.huya.search.hash;

import java.util.Collection;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/12.
 */
public class VirtualNodeImpl<A extends AssignObject> extends VirtualNode<A> {

    private Node<A> node;

    private int id;

    private int hash;

    public VirtualNodeImpl(Node<A> node, int id) {
        this.node = node;
        this.id = id;
        this.hash = node.hash(id);
    }

    @Override
    public int getVirtualId() {
        return id;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void add(A assignObject) {
        node.add(id, assignObject);
    }

    @Override
    public void remove(A assignObject) {
        node.remove(id, assignObject);
    }

    @Override
    public void removeAll(Collection<A> handOverSet) {
        node.removeAll(id, handOverSet);
    }

    @Override
    public void addAll(Collection<A> handOverSet) {
        node.addAll(id, handOverSet);
    }

    @Override
    public Set<A> getAllAssignObject() {
        return node.getAllAssignObject();
    }

    @Override
    public Set<A> getThisAssignObject() {
        return node.getAssignObject(id);
    }

    @Override
    public int hash() {
        return hash;
    }

    @Override
    public int hashCode() {
        return node.hashCode() * 31 + id ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualNode) {
            if (obj == this) return true;
            VirtualNode virtualNode = (VirtualNode)obj;
            return id == virtualNode.getVirtualId() && node == virtualNode.getNode();
        }
        return false;
    }

}
