package com.huya.search.index.data;

import com.huya.search.rpc.RpcResult;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public interface NodeMerger {

    RpcResult merger(List<Future<RpcResult>> futures);

}
