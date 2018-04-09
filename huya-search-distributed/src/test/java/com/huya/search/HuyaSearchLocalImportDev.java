package com.huya.search;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/17.
 */
public class HuyaSearchLocalImportDev {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        HuyaSearchLocalImport.main(new String[]{"test",
                "/Users/geekcat/workspace/huya-search-workspace/localImport.txt",
                "1516172400000", "1516183200000",
                "com.huya.search.localImport.FileLogSource",
                "com.huya.search.facing.convert.TestDataConvert"
        });
    }
}
