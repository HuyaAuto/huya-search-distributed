package com.huya.search;

import com.huya.search.util.OsUtil;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/26.
 */
public class HuyaSearchMasterDev {

    public static void main(String[] args) {
        if (OsUtil.isWindows()) {
            System.setProperty("java.security.auth.login.config", "E:/other/huya-search/huya-search-distributed/temp/conf/kafka_client_jaas.conf"); // 环境变量添加，需要输入配置文件的路径
        }
        HuyaSearchMaster.main(args);
    }
}
