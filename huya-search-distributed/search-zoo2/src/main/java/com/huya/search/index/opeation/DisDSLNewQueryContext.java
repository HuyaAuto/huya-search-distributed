package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.*;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.opeation.builder.DisDSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public class DisDSLNewQueryContext extends DSLNewQueryContext implements DisQueryContext {

    public static DisDSLNewQueryContext newInstance(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        return new DisDSLNewQueryContext(disDSLSortQueryBuilder);
    }


    protected DisDSLNewQueryContext(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        super(disDSLSortQueryBuilder);
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
            // 有分组（必须使用排序字段，为什么要这样规定呢？———— 如果在没有自定某字段排序的前提下
            // 对数据进行 limitSize 查询往往得到的结果都是同一分组内的结果，这样就变得毫无意义了），
            // 将各个分区的结果合并，排序前 limitSize
            return new AbstractShardMerger(merger){};
        }
    }

    @Override
    public NodeMerger getNodeMerger() {
        return new AbstractLimitNodeMerger(getMerger(), getLimit().size()){};
    }

    public static class DisDSLNewQueryContextSerializer extends Serializer<DisDSLNewQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLNewQueryContext object) {
            kryo.writeObject(output, object.dslQueryBuilder);
        }

        @Override
        public DisDSLNewQueryContext read(Kryo kryo, Input input, Class<DisDSLNewQueryContext> type) {
            return new DisDSLNewQueryContext(kryo.readObject(input, DisDSLSortQueryBuilder.class));
        }

    }
}
