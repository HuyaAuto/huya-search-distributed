package com.huya.search.monitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.*;
import com.yy.dc.saas.message.NotSupprtChannelException;
import com.yy.dc.saas.message.svr.IMessagePusherService;
import com.yy.dc.svr.Protocol;
import com.yy.dc.svr.bus.sdk.ServiceConnection;
import com.yy.dc.svr.bus.sdk.ServiceStatement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 通过sql方式预警
 *
 * @author ZhangXueJun
 * @date 2018年04月08日
 */
public abstract class AbstractSqlMonitor implements AbstractMonitor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String sql_monitor = "select * from %s where pr >= '%s' and pr < '%s'   order by NEW_DOC, timestamp desc limit 1";

    private String table;
    private int delayRange;

    private static IMessagePusherService messagePusherService;

    static {
        try {
            ServiceConnection connection = new ServiceConnection("MessageService");
            ServiceStatement svrExecutor = null;
            if (connection.supportProtocol(com.yy.dc.svr.Protocol.HTTP)) {
                svrExecutor = connection.createPackStatement(com.yy.dc.svr.Protocol.HTTP);
            } else if (connection.supportProtocol(com.yy.dc.svr.Protocol.HTTPS)) {
                svrExecutor = connection.createPackStatement(Protocol.HTTPS);
            }

            if (svrExecutor != null) {
                messagePusherService = svrExecutor.createProxyStatement(IMessagePusherService.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public int getDelayRange() {
        if (delayRange == 0) {
            return DEFAULT_DELAY;
        }
        return delayRange;
    }

    @Override
    public void setDelayRange(int delayRange) {
        this.delayRange = delayRange;
    }

    @Override
    public void execMonitor() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://14.29.58.111:28888/search/sql";
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        Date endTime = DateUtils.addMinutes(new Date(), -1);
        Date startTime = DateUtils.addMinutes(endTime, -getDelayRange());

        String sql = String.format(sql_monitor,
                getTable(),
                DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss"),
                DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")
        );

        RequestBody body = RequestBody.create(mediaType, sql);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        String jsonStr = null;
        try {
            Response response = client.newCall(request).execute();
            jsonStr = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            int code = jsonObject.getIntValue("code");
            if (code != 200) {
                sendMsg("日志索引服务查询响应错误！" + getTable(), "日志索引服务查询响应错误！" + getTable());
                return;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (CollectionUtils.isEmpty(jsonArray)) {
                sendMsg("日志索引服务查询延迟！" + getTable(), "日志索引服务查询延迟！" + getTable());
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sendMsg("日志索引服务查询服务错误！" + getTable(), "日志索引服务查询服务错误！" + getTable());
        }
        logger.info("日志索引服务查询成功！"+ getTable() + " " + jsonStr);
    }

    private void sendMsg(String message, String content) {
        try {
            messagePusherService.sendMessageToAccounts(WARN_USER_ARR,
                    message, content, "",
                    new String[]{"Mail", "YYPop", "SMS"});
        } catch (NotSupprtChannelException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
