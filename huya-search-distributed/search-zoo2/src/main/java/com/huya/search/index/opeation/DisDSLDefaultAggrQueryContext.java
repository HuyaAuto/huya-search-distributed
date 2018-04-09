package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.*;
import com.huya.search.index.data.merger.TimeCountMerger;
import com.huya.search.index.opeation.builder.DSLQueryBuilder;
import com.huya.search.index.opeation.builder.DisDSLQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class DisDSLDefaultAggrQueryContext extends DSLDefaultAggrQueryContext implements DisQueryContext {

    public static DisDSLDefaultAggrQueryContext newInstance(DisDSLQueryBuilder disDSLQueryBuilder) {
        return new DisDSLDefaultAggrQueryContext(disDSLQueryBuilder);
    }

    protected DisDSLDefaultAggrQueryContext(DisDSLQueryBuilder disDSLQueryBuilder) {
        super(disDSLQueryBuilder);
    }

    protected DisDSLDefaultAggrQueryContext(DSLQueryBuilder dslQueryBuilder) {
        super(dslQueryBuilder);
    }

    @Override
    public ShardMerger getShardMerger() {
        return AggrShardMerger.newInstance(new TimeCountMerger());
    }

    @Override
    public NodeMerger getNodeMerger() {
        return AggrNodeMerger.newInstance(new TimeCountMerger());
    }

    public static class DisDSLDefaultAggrQueryContextSerializer extends Serializer<DisDSLDefaultAggrQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLDefaultAggrQueryContext object) {
            kryo.writeObject(output, object.dslQueryBuilder);
        }

        @Override
        public DisDSLDefaultAggrQueryContext read(Kryo kryo, Input input, Class<DisDSLDefaultAggrQueryContext> type) {
            return new DisDSLDefaultAggrQueryContext(kryo.readObject(input, DisDSLQueryBuilder.class));
        }

    }

}
