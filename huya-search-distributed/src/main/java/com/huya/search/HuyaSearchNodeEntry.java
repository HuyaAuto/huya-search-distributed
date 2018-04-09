package com.huya.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.NodeEntry;
import com.huya.search.settings.Settings;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/9.
 */
@Singleton
public class HuyaSearchNodeEntry implements NodeEntry {

    private long startTimestamp;

    private String serviceHost;

    private int servicePort;

    private String serviceUrl;

    @Inject
    public HuyaSearchNodeEntry(@Named("Node")Settings settings, @Named("Host") String host, @Named("Port") int port) {
        this.startTimestamp = System.currentTimeMillis();
        this.servicePort = port;
        this.serviceHost = host;
        this.serviceUrl     = this.serviceHost + ":" + this.servicePort;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public String getServiceHost() {
        return serviceHost;
    }

    @Override
    public int getServicePort() {
        return servicePort;
    }

    @Override
    public String getServiceUrl() {
        return serviceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof NodeBaseEntry) {
            NodeBaseEntry that = (NodeBaseEntry) o;
            return servicePort == that.getServicePort() && serviceHost.equals(that.getServiceHost());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = serviceHost.hashCode();
        result = 31 * result + servicePort;
        return result;
    }

    @Override
    public String toString() {
        return "HuyaSearchNodeEntry{" +
                "startTimestamp=" + startTimestamp +
                ", serviceHost='" + serviceHost + '\'' +
                ", servicePort=" + servicePort +
                ", serviceUrl='" + serviceUrl + '\'' +
                '}';
    }
}
