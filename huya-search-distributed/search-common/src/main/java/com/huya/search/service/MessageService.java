package com.huya.search.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.settings.Settings;
import com.yy.dc.saas.message.svr.IMessagePusherService;
import com.yy.dc.svr.Protocol;
import com.yy.dc.svr.bus.sdk.ServiceConnection;
import com.yy.dc.svr.bus.sdk.ServiceStatement;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/17.
 */
@Singleton
public class MessageService {

    private static final String MSG = "msg_";

    private static final String[] WAYS = new String[]{"Mail","YYPop","SMS"};

    private static final String TITLE = "huya-search";

    private static final String HOME_URL = "search.huya.com";

    private String[] msgers;

    @Inject
    public MessageService(Settings settings) {
        this.msgers = settings.getAsArray(MSG);
    }

    public void send(String msg) throws Exception {
        ServiceConnection authConnection = new ServiceConnection("MessageService");
        ServiceStatement svrExecutor=null;
        if (authConnection.supportProtocol(Protocol.HTTP)) {
            svrExecutor = authConnection.createPackStatement(Protocol.HTTP);
        } else if (authConnection.supportProtocol(Protocol.HTTPS)) {
            svrExecutor = authConnection.createPackStatement(Protocol.HTTPS);
        }
        IMessagePusherService iMessagePusherService=svrExecutor.createProxyStatement(IMessagePusherService.class);
        iMessagePusherService.sendMessageToAccounts(msgers, TITLE, msg, HOME_URL, WAYS);
    }

    public void send(Exception e) throws Exception {
        send(e.getMessage());
    }

    public void send(String msg, Exception e) throws Exception {
        send(msg + "\n" + e.getMessage());
    }
}
