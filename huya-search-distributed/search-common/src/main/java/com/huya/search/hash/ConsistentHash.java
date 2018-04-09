package com.huya.search.hash;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/12.
 */
public abstract class ConsistentHash<N extends Node<A>, A extends AssignObject> implements Iterable<VirtualNode<A>> {

    private static final int DEFAULT_VIRTUAL_NODE_NUM = 3;

    private int virtualNodeNum;

    private TreeMap<Integer, VirtualNode<A>> treeMap = new TreeMap<>();

    public ConsistentHash(int virtualNodeNum) {
        this.virtualNodeNum = virtualNodeNum;
    }

    public ConsistentHash() {
        this(DEFAULT_VIRTUAL_NODE_NUM);
    }

    public synchronized void addNode(N node) {
        boolean isMaxHash;
        boolean isMinHash;

        for (int i = 0; i < virtualNodeNum; i++) {
            isMaxHash = false;
            isMinHash = false;
            VirtualNode<A> virtualNode = new VirtualNodeImpl<>(node, i);
            int hash = virtualNode.hash();
            Map.Entry<Integer, VirtualNode<A>> entry = treeMap.higherEntry(hash);

            Map.Entry<Integer, VirtualNode<A>> firstEntry = treeMap.firstEntry();


            if (entry == null) {
                entry = firstEntry;
                isMaxHash = true;
            }
            else {
                if (hash < firstEntry.getKey()) {
                    isMinHash = true;
                }
            }

            if (entry != null) {
                VirtualNode<A> higherVirtualNode = entry.getValue();
                Set<A> handOverSet = new HashSet<>();
                if (isMaxHash) {
                    higherVirtualNode.getThisAssignObject().forEach(assignObject -> {
                        int assignObjectHash = assignObject.hash();
                        int higherVirtualNodeHash = higherVirtualNode.hash();
                        if (assignObjectHash <= hash && assignObjectHash > higherVirtualNodeHash) {
                            handOverSet.add(assignObject);
                        }
                    });
                }
                else if (isMinHash) {
                    higherVirtualNode.getThisAssignObject().forEach(assignObject -> {
                        int assignObjectHash = assignObject.hash();
                        int higherVirtualNodeHash = higherVirtualNode.hash();
                        if (assignObjectHash > higherVirtualNodeHash || assignObjectHash <= hash) {
                            handOverSet.add(assignObject);
                        }
                    });
                }
                else {
                    higherVirtualNode.getThisAssignObject().forEach(assignObject -> {
                        int assignObjectHash = assignObject.hash();
                        if (assignObjectHash <= hash) {
                            handOverSet.add(assignObject);
                        }
                    });
                }

                if (handOverSet.size() > 0) {

                    Node providerNode = higherVirtualNode.getNode();
                    Node acquirerNode = virtualNode.getNode();

                    if (!providerNode.equals(acquirerNode)) {
                        joinSalverHandOver(providerNode, acquirerNode, handOverSet);
                    }
                    higherVirtualNode.removeAll(handOverSet);
                    virtualNode.addAll(handOverSet);
                }
            }

            treeMap.put(hash, virtualNode);
        }
    }

    public synchronized void removeNode(N node) {
        for (int i = 0; i < virtualNodeNum; i++) {
            VirtualNode<A> virtualNode = new VirtualNodeImpl<>(node, i);
            virtualNode = treeMap.get(virtualNode.hash());
            Map.Entry<Integer, VirtualNode<A>> higherVirtualNodeEntry = treeMap.higherEntry(virtualNode.hash());
            if (higherVirtualNodeEntry == null) {
                higherVirtualNodeEntry = treeMap.firstEntry();
            }
            VirtualNode<A> higherVirtualNode = higherVirtualNodeEntry.getValue();

            Set<A> handOverSet = virtualNode.getThisAssignObject();

            if (handOverSet.size() > 0) {

                Node providerNode = virtualNode.getNode();
                Node acquirerNode = higherVirtualNode.getNode();

                if (!providerNode.equals(acquirerNode)) {
                    lostSalverHandOver(providerNode, acquirerNode, handOverSet);
                }

                higherVirtualNode.addAll(handOverSet);
            }

            treeMap.remove(virtualNode.hash());
        }
    }

    public synchronized void addAssignObject(A assignObject) {
        if (treeMap.size() > 0) {

            int hash = assignObject.hash();
            Map.Entry<Integer, VirtualNode<A>> entry = treeMap.ceilingEntry(hash);
            if (entry == null) entry = treeMap.firstEntry();

            assert entry != null;

            VirtualNode<A> virtualNode = entry.getValue();
            assign(virtualNode.getNode(), assignObject);
            virtualNode.add(assignObject);
        }
    }

    public synchronized void removeAssignObject(A assignObject) {
        if (treeMap.size() > 0) {

            int hash = assignObject.hash();
            Map.Entry<Integer, VirtualNode<A>> entry = treeMap.ceilingEntry(hash);
            if (entry == null) entry = treeMap.firstEntry();

            assert entry != null;

            VirtualNode<A> virtualNode = entry.getValue();
            discharge(virtualNode.getNode(), assignObject);
            virtualNode.remove(assignObject);
        }
    }

    @Override
    public Iterator<VirtualNode<A>> iterator() {
        return treeMap.values().iterator();
    }

    public abstract void joinSalverHandOver(Node providerNode, Node acquirerNode, Set<A> handOverSet);

    public abstract void lostSalverHandOver(Node lostNode, Node acquirerNode, Set<A> handOverSet);

    public abstract void assign(Node acquirerNode, A assignObject);

    public abstract void discharge(Node acquirerNode, A assignObject);
}
