package com.huya.search.index.data;

import com.huya.search.index.data.impl.PartitionDocs;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.rpc.RpcResult;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class AbstractNodeMerger implements NodeMerger {

    private Merger merger;

    protected AbstractNodeMerger(Merger merger) {
        this.merger = merger;
    }

    @Override
    public RpcResult merger(List<Future<RpcResult>> futures) {
        int tag = 0;
        for (Future<RpcResult> future : futures) {
            try {
                RpcResult rpcResult = future.get();
                merger.add(PartitionDocs.newInstance(String.valueOf(tag++), rpcResult.getResult()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
        return ResultUtil.tran(merger.result());
    }
}
