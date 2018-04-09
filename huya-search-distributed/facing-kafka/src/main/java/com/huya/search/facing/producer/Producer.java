package com.huya.search.facing.producer;

public interface Producer {

    void producerData(String topic, String scriptName, long num);
}
