package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.NodeMerger;
import com.huya.search.index.data.ShardMerger;
import com.huya.search.index.opeation.builder.DisDSLSortQueryBuilder;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/6.
 */
public class DisDSLMixNewQueryContext extends DSLMixNewQueryContext implements DisQueryContext {

    public static DisDSLMixNewQueryContext newInstance(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        return new DisDSLMixNewQueryContext(disDSLSortQueryBuilder);
    }


    DisDSLMixNewQueryContext(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        super(disDSLSortQueryBuilder);
    }

    @Override
    public ShardMerger getShardMerger() {
        return null;
    }

    @Override
    public NodeMerger getNodeMerger() {
        return null;
    }

    public static class DisDSLMixNewQueryContextSerializer extends Serializer<DisDSLMixNewQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLMixNewQueryContext object) {
            kryo.writeObject(output, object.dslQueryBuilder);
        }

        @Override
        public DisDSLMixNewQueryContext read(Kryo kryo, Input input, Class<DisDSLMixNewQueryContext> type) {
            return new DisDSLMixNewQueryContext(kryo.readObject(input, DisDSLSortQueryBuilder.class));
        }

    }
}
