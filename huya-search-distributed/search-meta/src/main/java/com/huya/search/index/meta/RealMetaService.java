package com.huya.search.index.meta;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;

import java.util.Iterator;
import java.util.Set;

@Singleton
public class RealMetaService extends MetaService {

    private MetaCollection collection;

    private MetaFollowerList metaFollowerList = new MetaFollowerList();


    @Inject
    public RealMetaService() {}

    @Override
    public synchronized void add(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        if (collection.contains(table)) {
            collection.put(table, metaDefine);
        }
        else {
            collection.put(table, metaDefine);
            noticeFollowerToOpen(metaDefine);
        }
    }

    @Override
    public synchronized void remove(String table) {
        if (collection.contains(table)) {
            close(table);
            collection.remove(table);
        }
        else {
            throw new MetaOperatorException("table is not exist");
        }
    }

    @Override
    public synchronized void refresh(String table) {
        TimelineMetaDefine metaDefine = collection.get(table);
        if (metaDefine != null) {
            if (metaDefine.isOpen()) {
                close(table);
            }
            open(table);
        }
    }

    @Override
    public synchronized void open(String table) {
        TimelineMetaDefine metaDefine = collection.open(table);
        if (metaDefine != null) {
            noticeFollowerToOpen(metaDefine);
        }
    }

    @Override
    public synchronized void close(String table) {
        TimelineMetaDefine metaDefine = collection.close(table);
        if (metaDefine != null) {
            noticeFollowerToClose(metaDefine);
        }
    }

    /**
     * 元数据关注者通过优先级从大到小依次打开
     * @param metaDefine 被关注的元数据
     */
    private void noticeFollowerToOpen(TimelineMetaDefine metaDefine) {
        Iterator<MetaFollower> desc = metaFollowerList.descIterator();
        while (desc.hasNext()) {
            MetaFollower follower = desc.next();
            follower.open(metaDefine);
        }
    }

    /**
     * 元数据关注者通过优先级从小到大依次关闭
     * @param metaDefine 被关注的元数据
     */
    private void noticeFollowerToClose(TimelineMetaDefine metaDefine) {
        Iterator<MetaFollower> asc = metaFollowerList.ascIterator();
        while (asc.hasNext()) {
            MetaFollower follower = asc.next();
            follower.close(metaDefine);
        }
    }

    @Override
    public synchronized void register(MetaFollower metaFollower) {
        metaFollowerList.add(metaFollower);
    }

    @Override
    public TimelineMetaDefine get(String table) {
        return collection.get(table);
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        return collection.iterator();
    }

    @Override
    public Set<String> getTables() {
        return collection.getTables();
    }

    @Override
    protected void doStart() throws SearchException {
        collection = RealMetaCollection.newInstance(Maps.newConcurrentMap());
    }

    @Override
    protected void doStop() throws SearchException {
        //do nothing
    }

    @Override
    protected void doClose() throws SearchException {
        Iterator<TimelineMetaDefine> iterator = collection.iterator();
        while (iterator.hasNext()) {
            TimelineMetaDefine metaDefine = iterator.next();
            if (metaDefine.isOpen()) {
                noticeFollowerToClose(metaDefine);
            }
        }
        collection.clear();
    }

    @Override
    public String getName() {
        return "RealMetaService";
    }
}
