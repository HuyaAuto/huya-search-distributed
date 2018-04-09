package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.QueryContext;
import com.huya.search.partition.PartitionCycle;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/9.
 */
public interface LuceneReader {

    String tag();

    PartitionCycle getPartitionCycle();

    ShardMetaDefine getShardMetaDefine();

    void refresh(boolean forced) throws IOException;

    void closeReader() throws IOException;

    void loadReader() throws IOException;

    boolean isLoadReader();
}
