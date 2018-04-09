package com.huya.search.util;

/**
 * @author ZhangXueJun
 * @date 2018年03月19日
 */
public class OsUtil {

    public enum Os {
        windows, linux, mac, invalid;
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0;
    }

}
