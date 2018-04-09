package com.huya.search.node;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/16.
 */
public class HuyaSearchWatcher implements Watcher {




    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState keeperState = event.getState();
        Event.EventType   eventType   = event.getType();
        String            path        = event.getPath();



    }
}
