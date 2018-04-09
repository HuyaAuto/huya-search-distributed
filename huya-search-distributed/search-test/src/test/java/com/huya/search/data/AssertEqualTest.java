package com.huya.search.data;

import com.huya.search.data.util.AssertList;
import com.huya.search.data.util.AssertMap;
import com.huya.search.data.util.AssertObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/7.
 */
public class AssertEqualTest {

    @Test
    public void assertMapTest() {
        Map<String, String> aMap = new HashMap<>();
        Map<String, String> bMap = new HashMap<>();

        Assert.assertTrue(AssertMap.equals(aMap, bMap));

        for (int i = 0; i < 10; i++) {
            aMap.put("key" + i, "value" + i);
            bMap.put("key" + i, "value" + i);
        }

        Assert.assertTrue(AssertMap.equals(aMap, bMap));

        bMap.put("bKey" , "bValue");

        Assert.assertTrue(!AssertMap.equals(aMap, bMap));

        aMap.put("aKey" , "aValue");

        Assert.assertTrue(!AssertMap.equals(aMap, bMap));
    }

    @Test
    public void assertListTest() {
        List<String> aList = Arrays.asList("1", "2", "5", "3", "4", "6");
        List<String> bList = Arrays.asList("1", "4", "5", "6", "2", "3");

        Assert.assertTrue(!AssertList.equals(aList, bList, false));
        Assert.assertTrue(AssertList.equals(aList, bList, true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void assertObjectTest() {
        Map aMap = new HashMap<>();
        Map bMap = new HashMap<>();

        aMap.put("1", Arrays.asList("1", "2", "5", "3", "4", "6"));
        aMap.put("2", Arrays.asList(1, 2, 3, 5, 6, 4));
        aMap.put(3, 123456L);

        bMap.put("1", Arrays.asList("1", "5", "3", "6", "4", "2"));
        bMap.put("2", Arrays.asList(2, 1, 3, 4, 6, 5));
        bMap.put(3, 123456F);

        Assert.assertTrue(AssertObject.equals(aMap, bMap));
    }
}
