package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.*;
import com.huya.search.index.data.merger.Merger;
import com.huya.search.index.data.merger.MergerFactory;
import com.huya.search.index.opeation.builder.DSLSortQueryBuilder;
import com.huya.search.index.opeation.builder.DisDSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/16.
 */
public class DisDSLSortQueryContext extends DSLSortQueryContext implements DisQueryContext {


    public static DisDSLSortQueryContext newInstance(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        return new DisDSLSortQueryContext(disDSLSortQueryBuilder);
    }

    private DisDSLSortQueryContext(DSLSortQueryBuilder dslSortQueryBuilder) {
        super(dslSortQueryBuilder);
    }

    @Override
    public ShardMerger getShardMerger() {
        return new AbstractLimitShardMerger(getMerger(), getLimit().top()){};
    }

    @Override
    public NodeMerger getNodeMerger() {
        Merger merger;
        if (getGroupByItems() == null) {
            merger = MergerFactory.createSortMerger(getSort(), getLimit().from(), getLimit().to());
        }
        else {
            merger = MergerFactory.createSortGroupMerger(getSort(), getGroupByItems().groupFields(), getMetaDefine(), getLimit().from(), getLimit().to());
        }
        return new AbstractNodeMerger(merger) {};
    }

    public static class DisDSLSortQueryContextSerializer extends Serializer<DisDSLSortQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLSortQueryContext object) {
            kryo.writeObject(output, object.dslQueryBuilder);
        }

        @Override
        public DisDSLSortQueryContext read(Kryo kryo, Input input, Class<DisDSLSortQueryContext> type) {
            return new DisDSLSortQueryContext(kryo.readObject(input, DisDSLSortQueryBuilder.class));
        }

    }
}
