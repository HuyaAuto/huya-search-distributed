package com.huya.search.monitor;

/**
 * @author ZhangXueJun
 * @date 2018年04月08日
 */
public interface AbstractMonitor {

    /**
     * 延迟多少分钟才预警,单位分钟
     */
    int DEFAULT_DELAY = 10;

    String[] WARN_USER_ARR = new String[] {"dw_zhangxuejun"};

    /**
     * 执行监控
     */
    void execMonitor();

    int getDelayRange();

    void setDelayRange(int delayRange);

    String getTable();

    void setTable(String table);
}
