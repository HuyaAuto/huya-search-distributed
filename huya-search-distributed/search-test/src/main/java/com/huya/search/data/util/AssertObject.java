package com.huya.search.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/7.
 */
public class AssertObject {

    private static final Logger LOG = LoggerFactory.getLogger(AssertMap.class);

    private static final double EPSILON = 0.000000000000001d;

    public static boolean equals(Object a, Object b) {
        return (a == null && b == null) || (a != null && b != null && equalsObject(a, b));
    }

    private static boolean equalsObject(Object a, Object b) {
        if (a instanceof Map && b instanceof  Map) {
            return AssertMap.equals((Map)a, (Map)b);
        }
        else if (a instanceof List && b instanceof List) {
            return AssertList.equals((List)a, (List)b, true);
        }
        else if (a instanceof Number && b instanceof Number) {
            BigDecimal aBig = new BigDecimal(String.valueOf(a));
            BigDecimal bBig = new BigDecimal(String.valueOf(b));
            return aBig.compareTo(bBig) == 0;
        }
        else {
            boolean flag = a.equals(b);
            if (!flag) LOG.error(a + " is not euqal " + b);
            return flag;
        }
    }

}
