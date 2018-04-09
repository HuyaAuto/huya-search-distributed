package com.huya.search.util;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/25.
 */
public class InstanceClassUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class<?> clazz = InstanceClassUtil.class.getClassLoader().loadClass(className);
        return (T) clazz.newInstance();
    }
}
