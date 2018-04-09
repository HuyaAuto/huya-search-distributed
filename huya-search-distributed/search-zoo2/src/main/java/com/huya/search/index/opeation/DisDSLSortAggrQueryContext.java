package com.huya.search.index.opeation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.*;
import com.huya.search.index.data.merger.*;
import com.huya.search.index.dsl.group.GroupByItems;
import com.huya.search.index.dsl.sorted.SortedItems;
import com.huya.search.index.opeation.builder.DisDSLSortQueryBuilder;
import org.apache.lucene.search.Sort;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/22.
 */
public class DisDSLSortAggrQueryContext extends DisDSLDefaultAggrQueryContext implements DisQueryContext {

    private DisDSLSortQueryBuilder disDSLSortQueryBuilder;

    public static DisDSLSortAggrQueryContext newInstance(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        return new DisDSLSortAggrQueryContext(disDSLSortQueryBuilder);
    }


    private DisDSLSortAggrQueryContext(DisDSLSortQueryBuilder disDSLSortQueryBuilder) {
        super(disDSLSortQueryBuilder);
        this.disDSLSortQueryBuilder = disDSLSortQueryBuilder;
    }


    @Override
    public ShardMerger getShardMerger() {
        return super.getShardMerger();
    }

    @Override
    public NodeMerger getNodeMerger() {
        GroupByItems groupByItems = getGroupByItems();
        DefaultAggrFunUnitSet defaultAggrFunUnitSet = getSelectItems().getNodeAggrFunSet();
        Sort sort = getSortedItems().sortInstance();
        if (groupByItems == null) {
            SortMerger sortMerger = new SortMerger(sort);
            return AggrNodeMerger.newInstance(new AggrSortMerger(defaultAggrFunUnitSet, sortMerger));
        }
        else {
            return AggrNodeMerger.newInstance(new AggrSortGroupMerger(defaultAggrFunUnitSet, sort, groupByItems.groupFields(), getMetaDefine()));
        }
    }

    private SortedItems getSortedItems() {
        return disDSLSortQueryBuilder.getSortedItems();
    }


    public static class DisDSLSortAggrQueryContextSerializer extends Serializer<DisDSLSortAggrQueryContext> {

        @Override
        public void write(Kryo kryo, Output output, DisDSLSortAggrQueryContext object) {
            kryo.writeObject(output, object.disDSLSortQueryBuilder);
        }

        @Override
        public DisDSLSortAggrQueryContext read(Kryo kryo, Input input, Class<DisDSLSortAggrQueryContext> type) {
            return new DisDSLSortAggrQueryContext(kryo.readObject(input, DisDSLSortQueryBuilder.class));
        }

    }
}
