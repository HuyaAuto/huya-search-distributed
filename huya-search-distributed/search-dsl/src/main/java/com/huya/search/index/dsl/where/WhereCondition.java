package com.huya.search.index.dsl.where;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Range;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.meta.*;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.search.BooleanQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public abstract class WhereCondition implements CorrespondTable {

    private TimelineMetaDefine timelineMetaDefine;

    private WhereStatement whereStatement;

    private BooleanQuery query = null;

    private PartitionRange range;

    public WhereCondition(TimelineMetaDefine timelineMetaDefine, WhereStatement whereStatement) {
        this.timelineMetaDefine = timelineMetaDefine;
        this.whereStatement = whereStatement;
    }

    public TimelineMetaDefine getTimelineMetaDefine() {
        return timelineMetaDefine;
    }

    public Set<Long> getCycleList() {
        TimelineMetaDefine metaDefine = getTimelineMetaDefine();
        PartitionGrain grain = metaDefine.getGrain();
        PartitionCycle cycle = metaDefine.getFirstPartitionCycle();

            //todo 添加元数据切割逻辑，将查询时间段按照元数据定义时间段分割，
            //todo 方便后面通过起始点构造 LuceneOperator
//            List<Range<Long>> ranges = range.getRanges();
//            ranges = metaCuttingRanges(ranges, metaDefine);
//
//            Set<Long> cycleSet = new HashSet<>();
//            for (Range<Long> rangeLong : ranges) {
//                cycleSet.addAll()
//            }

        return getRange().getPartitionCycleList(grain, cycle);
    }

    private PartitionRange getRange() {
        if (range == null) {
            range = whereStatement.getPartitionRange();
        }
        return range;
    }

    public long getLowCycle() {
        return getRange().getLow();
    }

    public long getUpCycle() {
        return getRange().getUp();
    }

    private List<Range<Long>> metaCuttingRanges(List<Range<Long>> ranges, TimelineMetaDefine metaDefine) {

        return null;
    }

    public BooleanQuery getQuery(IntactMetaDefine intactMetaDefine) throws BuildLuceneQueryException {
        if (query == null) {
            query = whereStatement.getQuery(intactMetaDefine);
        }
        return query;
    }

    @Override
    public String getTable() {
        return timelineMetaDefine.getTable();
    }


    public static class WhereConditionSerializer extends Serializer<WhereCondition> {

        @Override
        public void write(Kryo kryo, Output output, WhereCondition object) {
            String table = object.getTable();
            Set<Long> cycleList = object.getCycleList();

            kryo.writeObject(output, table);
            kryo.writeObject(output, cycleList.size());
            for (Long cycle : cycleList) {
                kryo.writeObject(output, cycle);
            }
            kryo.writeObject(output, object.whereStatement);
        }

        @Override
        public WhereCondition read(Kryo kryo, Input input, Class<WhereCondition> type) {
            String table = kryo.readObject(input, String.class);

            TimelineMetaDefine metaDefine = ModulesBuilder.getInstance()
                    .createInjector().getInstance(MetaService.class).get(table);

            int cycleSize = kryo.readObject(input, Integer.class);
            Set<Long> cycleList = new HashSet<>();
            for (int i = 0; i < cycleSize; i++) {
                cycleList.add(kryo.readObject(input, Long.class));
            }

            WhereStatement whereStatement = kryo.readObject(input, WhereStatement.class);

            return new WhereCondition(metaDefine, whereStatement) {
                @Override
                public Set<Long> getCycleList() {
                    return cycleList;
                }

            };

        }

    }
}
