package com.huya.search;

public enum GlobalServicePriority {

    LOCAL_IMPORT_SERVICE(128),
    ZOOKEEPER_SERVICE(128),
    LUCENE_SERVICE(64),
    INDEX_ENGINE(32),
    KAFKA_PRODUCER_SERVICE(16),
    KAFKA_SUBSCRIBE_SERVICE(8),
    META_SERVICE(4),
    NODE_SERVICE(2),
    NETTY_SERVICE(1);

    private int priority;

    GlobalServicePriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
