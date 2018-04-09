package com.huya.search.index.lucene;

import com.huya.search.index.meta.ShardMetaDefine;
import com.huya.search.index.opeation.InsertContext;
import com.huya.search.partition.PartitionCycle;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public interface LuceneWriter {

    PartitionCycle getPartitionCycle();

    ShardMetaDefine getShardMetaDefine();

    void write(InsertContext context) throws IOException;

    void addIndexes(Directory ... dirs) throws IOException;

    long writeRamUsed();

    void flush() throws IOException;

    void forceMerge(int num) throws IOException;

    void closeWriter() throws IOException;

    void rollbackCloseWriter() throws IOException;

    void loadWriter() throws IOException;

    boolean isLoadWriter();

}
