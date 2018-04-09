package com.huya.search.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.settings.Settings;
import com.yy.dc.saas.message.svr.IMessagePusherService;
import com.yy.dc.svr.Protocol;
import com.yy.dc.svr.bus.sdk.ServiceConnection;
import com.yy.dc.svr.bus.sdk.ServiceStatement;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/28.
 */

@Singleton
public class WarnService {

    public static WarnService getInstance() {
        return ModulesBuilder.getInstance().createInjector().getInstance(WarnService.class);
    }

    private String[] warnTarget;

    @Inject
    public WarnService(@Named("Settings") Settings settings) {
        warnTarget = settings.getAsArray("warnTarget", null);
    }

    public void send(String msg, Throwable cause) {
        ServiceConnection authConnection;
        try {
            authConnection = new ServiceConnection("MessageService");
            ServiceStatement svrExecutor = null;
            if (authConnection.supportProtocol(Protocol.HTTP)) {
                svrExecutor = authConnection.createPackStatement(Protocol.HTTP);
            } else if (authConnection.supportProtocol(Protocol.HTTPS)) {
                svrExecutor = authConnection.createPackStatement(Protocol.HTTPS);
            }

            if (svrExecutor != null) {
                IMessagePusherService iMessagePusherService = svrExecutor.createProxyStatement(IMessagePusherService.class);
                iMessagePusherService.sendMessageToAccounts(warnTarget, "日志索引服务", msg + "\n" + cause.getMessage(), "", new String[]{"Mail", "YYPop", "SMS"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
