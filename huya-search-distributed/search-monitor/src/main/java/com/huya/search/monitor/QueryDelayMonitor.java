package com.huya.search.monitor;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 查询延迟预警, 对最终结果预警
 *
 * @author ZhangXueJun
 * @date 2018年04月08日
 */
public class QueryDelayMonitor extends AbstractSqlMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(QueryDelayMonitor.class);

    public static void main(String[] args) {
//        args = new String[]{"gamelive_huya_hysignal_tx_response_time_log"};
        LOG.info("args:" + ArrayUtils.toString(args));
        Assert.state(args != null && args.length >=1, "参数错误!");

        /**
         * 监控的表名
         */
        String table = args[0];
        QueryDelayMonitor queryDelayMonitor = new QueryDelayMonitor();
        if (args.length >=2) {
            int delay = Integer.valueOf(args[1]);
            queryDelayMonitor.setDelayRange(delay);
        }

        queryDelayMonitor.setTable(table);
        queryDelayMonitor.execMonitor();

        // 延迟多少分钟才预警
    }
}
