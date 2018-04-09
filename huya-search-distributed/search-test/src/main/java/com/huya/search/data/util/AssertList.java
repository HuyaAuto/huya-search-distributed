package com.huya.search.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
/**
 * Created by zhangyiqun1@yy.com on 2017/10/7.
 */
public class AssertList {

    private static final Logger LOG = LoggerFactory.getLogger(AssertList.class);

    public static boolean equals(List a, List b, boolean sort) {
        return (a == null && b == null) || (a != null && b != null && equalsList(a, b, sort));
    }

    private static boolean equalsList(List a, List b, boolean sort) {
        int aSize = a.size(), bSize = b.size();

        if (aSize != bSize) {
            LOG.error("size not equal, a.size = " + aSize + "  b.size = " + bSize);
            return false;
        }
        else {
            Object[] aArray = a.toArray();
            Object[] bArray = b.toArray();

            return sort ? needSortEquals(aArray, bArray) : noNeedSortEquals(aArray, bArray);
        }
    }

    private static boolean needSortEquals(Object[] aArray, Object[] bArray) {
        Arrays.sort(aArray);
        Arrays.sort(bArray);
        return noNeedSortEquals(aArray, bArray);
    }

    private static boolean noNeedSortEquals(Object[] aArray, Object[] bArray) {
        for (int i = 0; i < aArray.length; i++) {
            if (!AssertObject.equals(aArray[i], bArray[i])) {
                LOG.error("point = " + i + "  value is not equal, a.value = " + aArray[i] + " b.value = " + bArray[i]);
                return false;
            }
        }
        return true;
    }
}
