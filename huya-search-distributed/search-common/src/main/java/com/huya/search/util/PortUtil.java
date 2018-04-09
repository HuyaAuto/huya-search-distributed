package com.huya.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/3.
 */
public class PortUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PortUtil.class);

    public static synchronized int getPortFromArray(int[] portArray) {
        for (int port : portArray) {
            if (available(port)) {
                LOG.info("HuyaSearch,bind port on:" + port);
                return port;
            }
        }
        throw new RuntimeException("not available port to get");
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    private static boolean available(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignore) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }


}
