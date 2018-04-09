package com.huya.search.node.salvers;

import com.huya.search.service.AbstractOrderService;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/18.
 */
public abstract class SalversService extends AbstractOrderService {

     abstract long getRegisterUnixTime();

}
