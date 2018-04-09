package com.huya.search.index.meta;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/14.
 */
public class RealMetaCollection implements MetaCollection {

    private Map<String, TimelineMetaDefine> collection;

    public static RealMetaCollection newInstance(Map<String, TimelineMetaDefine> map) {
        return new RealMetaCollection(map);
    }

    private RealMetaCollection(Map<String, TimelineMetaDefine> collection) {
        this.collection = collection;
    }

    @Override
    public boolean remove(String table) {
        return collection.remove(table) != null;
    }

    @Override
    public void put(String table, TimelineMetaDefine metaDefine) {
        collection.put(table, metaDefine);
    }

    @Override
    public TimelineMetaDefine get(String table) {
        return collection.get(table);
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        return new TimelineMetaDefineIterator();
    }

    @Override
    public boolean contains(String table) {
        return collection.containsKey(table);
    }

    @Override
    public TimelineMetaDefine open(String table) {
        TimelineMetaDefine metaDefine = collection.get(table);
        if (metaDefine != null && !metaDefine.isOpen()) {
            metaDefine.open();
            return metaDefine;
        }
        return null;
    }

    @Override
    public TimelineMetaDefine close(String table) {
        TimelineMetaDefine metaDefine = collection.get(table);
        if (metaDefine != null && metaDefine.isOpen()) {
            metaDefine.close();
            return metaDefine;
        }
        return metaDefine;
    }

    @Override
    public void clear() {
        collection.clear();
    }

    @Override
    public Set<String> getTables() {
        return collection.keySet();
    }


    private class TimelineMetaDefineIterator implements Iterator<TimelineMetaDefine> {

        private Iterator<Map.Entry<String, TimelineMetaDefine>> iterator;

        TimelineMetaDefineIterator() {
            iterator = collection.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public TimelineMetaDefine next() {
            return iterator.next().getValue();
        }
    }
}
