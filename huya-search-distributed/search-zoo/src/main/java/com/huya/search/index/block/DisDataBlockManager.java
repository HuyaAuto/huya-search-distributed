package com.huya.search.index.block;

import com.huya.search.index.block.feature.DisIndexFeature;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.master.MasterNotAppearedException;
import com.huya.search.node.salvers.Salvers;
import com.huya.search.service.AbstractOrderService;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public abstract class DisDataBlockManager extends AbstractOrderService implements DisIndexFeature {

    List<SalversModListener> listenerList = new Vector<>();

    public abstract Salvers getAllSalvers();

    public void addSalversModListener(SalversModListener salversModListener) {
        listenerList.add(salversModListener);
    }

    public static class NodeBaseEntryImpl implements NodeBaseEntry {

        private String serviceHost;

        private int servicePort;

        private String serviceUrl;

        public static NodeBaseEntryImpl newInstance(String serviceUrl) {
            if (serviceUrl == null || Objects.equals(serviceUrl, "")) {
                throw new MasterNotAppearedException();
            }
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

            NodeBaseEntryImpl that = (NodeBaseEntryImpl) o;

            if (servicePort != that.servicePort) return false;
            return serviceHost.equals(that.serviceHost);
        }

        @Override
        public int hashCode() {
            int result = serviceHost.hashCode();
            result = 31 * result + servicePort;
            return result;
        }
    }
}
