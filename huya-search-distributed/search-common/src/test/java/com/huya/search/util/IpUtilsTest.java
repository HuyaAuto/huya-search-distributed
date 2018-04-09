package com.huya.search.util;

import org.junit.Test;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/16.
 */
public class IpUtilsTest {

    @Test
    public void innerIpTest() {
        String ip = IpUtils.getInnerIp();
        assert ip != null;
        System.out.println(ip);
    }

}
