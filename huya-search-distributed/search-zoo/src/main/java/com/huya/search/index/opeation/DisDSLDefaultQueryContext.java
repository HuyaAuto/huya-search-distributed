package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.*;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.opeation.builder.DisDSLQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public class DisDSLDefaultQueryContext extends DSLDefaultQueryContext implements DisQueryContext {

    public static DisDSLDefaultQueryContext newInstance(DisDSLQueryBuilder disDSLQueryBuilder) {
        return new DisDSLDefaultQueryContext(disDSLQueryBuilder);
    }

    protected DisDSLDefaultQueryContext(DisDSLQueryBuilder disDSLQueryBuilder) {
        super(disDSLQueryBuilder);
    }

    @Override
    public ShardMerger getShardMerger() {
        GroupByItems items = getGroupByItems();
        Merger merger = getMerger();
        int limitSize = getLimit().size();
        if (items == null) {
            //无分组，则将各个分区的数据合并，简单的去前 limitSize 即可
            return new AbstractLimitShardMerger(merger, limitSize) {};
        }
        else {
            //有分组（必须使用排序字段），将各个分区的结果合并，排序前 limitSize
            return new AbstractShardMerger(merger){};
        }
    }

    @Override
    public NodeMerger getNodeMerger() {
        return new AbstractLimitNodeMerger(getMerger(), getLimit().size()){};
    }

    public static class DisDSLDefaultQueryContextSerializer extends Serializer<DisDSLDefaultQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLDefaultQueryContext object) {
            kryo.writeObject(output, object.dslQueryBuilder);
        }

        @Override
        public DisDSLDefaultQueryContext read(Kryo kryo, Input input, Class<DisDSLDefaultQueryContext> type) {
            return new DisDSLDefaultQueryContext(kryo.readObject(input, DisDSLQueryBuilder.class));
        }

    }
}
