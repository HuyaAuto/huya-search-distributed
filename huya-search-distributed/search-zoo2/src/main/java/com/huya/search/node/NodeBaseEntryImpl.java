package com.huya.search.node;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/27.
 */

import java.util.Objects;

public class NodeBaseEntryImpl implements NodeBaseEntry {

    private String serviceHost;

    private int servicePort;

    private String serviceUrl;

    public static NodeBaseEntryImpl newInstance(String serviceUrl) {
        assert serviceUrl != null && !Objects.equals(serviceUrl, "");

        NodeBaseEntryImpl nodeBaseEntry = new NodeBaseEntryImpl();
        nodeBaseEntry.serviceUrl = serviceUrl;
        String[] temp = nodeBaseEntry.serviceUrl.split(":");
        nodeBaseEntry.serviceHost = temp[0];
        nodeBaseEntry.servicePort = Integer.parseInt(temp[1]);
        return nodeBaseEntry;
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
    public String toString() {
        return "NodeBaseEntryImpl{" +
                "serviceHost='" + serviceHost + '\'' +
                ", servicePort=" + servicePort +
                ", serviceUrl='" + serviceUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
}
