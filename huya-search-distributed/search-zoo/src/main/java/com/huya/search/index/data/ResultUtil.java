package com.huya.search.index.data;

import com.huya.search.index.data.impl.PartitionDocs;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.rpc.RpcResult;
import com.huya.search.rpc.RpcResultRow;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/17.
 */
public class ResultUtil {

    public static RpcResult tran(QueryResult<? extends Iterable<IndexableField>> result) {
        List<RpcResultRow> rpcResultRows = new ArrayList<>(result.size());

        List<? extends Iterable<IndexableField>> temp = result.getCollection();

        for (Iterable<IndexableField> iterable : temp) {
            RpcResultRow row = new RpcResultRow(iterable.iterator());
            rpcResultRows.add(row);
        }

        return new RpcResult(rpcResultRows);
    }

    public static void mergerFutureResult(Merger merger, List<Future<QueryResult<? extends Iterable<IndexableField>>>> queryResultFutureList) {
        int tag = 0;
        for (Future<QueryResult<? extends Iterable<IndexableField>>> future : queryResultFutureList) {
            try {
                QueryResult<? extends Iterable<IndexableField>> queryResult = future.get();
                merger.add(PartitionDocs.newInstance(String.valueOf(tag ++), queryResult.getCollection()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
