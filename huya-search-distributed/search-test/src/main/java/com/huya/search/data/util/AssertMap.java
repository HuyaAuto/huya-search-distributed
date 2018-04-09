package com.huya.search.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/7.
 */
public class AssertMap {

    private static final Logger LOG = LoggerFactory.getLogger(AssertMap.class);

    public static boolean equals(Map<?, ?> a, Map<?, ?> b) {
        return (a == null && b == null) || (a != null && b != null && equalsMap(a, b));
    }

    private static boolean equalsMap(Map<?, ?> a, Map<?, ?> b) {
        int aSize = a.size(), bSize = b.size();

        if (aSize != bSize) {
            LOG.error("size not equal, a.size = " + aSize + "  b.size = " + bSize);
            return false;
        }
        else {
            for (Map.Entry entry : a.entrySet()) {
                Object key   = entry.getKey();
                Object aValue = entry.getValue();
                Object bValue = b.get(key);
                if (!AssertObject.equals(aValue, bValue)) {
                    LOG.error("key = " + key + "  value is not equal, a.value = " + aValue + " b.value = " + bValue);
                    return false;
                }
            }
            return true;
        }
    }
}
