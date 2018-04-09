package com.huya.search.index.dsl.where.expression;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Range;
import com.huya.search.index.dsl.parse.ParseExpection;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.dsl.where.expression.builder.DateBuilder;
import com.huya.search.index.dsl.where.expression.timestamp.TimestampExplain;
import com.huya.search.index.dsl.where.expression.timestamp.TimestampFactory;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.index.meta.impl.FieldFactory;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.search.Query;

import java.util.List;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/24.
 */
public class TimestampExpression extends BooleanExpression {

    public static final String PR = "pr";

    private PartitionRange partitionRange;

    private TimestampExplain explain;

    public static TimestampExpression newInstance(ExpressionOperator operator, String value) {
        return new TimestampExpression(operator, value);
    }

    private TimestampExpression(ExpressionOperator operator, String value) {
        super(FieldFactory.TIME_STAMP, operator, value);
        this.explain = TimestampFactory.getExplain(value);
    }

    public void explain() {

        switch (getOperator()) {
            case EQUAL: partitionRange = PartitionRange.getByRange(explain.equal()); break;
            case MT:    partitionRange = PartitionRange.getByRange(explain.mt());    break;
            case MTOE:  partitionRange = PartitionRange.getByRange(explain.mtoe());  break;
            case LT:    partitionRange = PartitionRange.getByRange(explain.lt());    break;
            case LTOE:  partitionRange = PartitionRange.getByRange(explain.ltoe());  break;
            case NOT:   partitionRange = PartitionRange.getByRanges(explain.not());  break;
            default: throw new ParseExpection("timestamp no support this operator :" + getOperator());
        }
    }

    @Override
    public boolean aboutTimestamp() {
        return true;
    }

    public PartitionRange getPartitionRange() {
        if (partitionRange == null) {
            explain();
        }
        return partitionRange;
    }

    @Override
    public Query getQuery(IntactMetaDefine intactMetaDefine, String logSvrApp, String logSvrModule, String logSvrType) throws BuildLuceneQueryException {
        PartitionRange partitionRange = getPartitionRange();
        List<Range<Long>> ranges = partitionRange.getRanges();
        DateBuilder builder = DateBuilder.create();
        return builder.rangesBuild(getField(), ranges);
    }

    public static class TimestampExpressionSerializer extends Serializer<TimestampExpression> {

        @Override
        public void write(Kryo kryo, Output output, TimestampExpression object) {
            kryo.writeObject(output, object.getOperator().toString());
            kryo.writeObject(output, object.getValue());
        }

        @Override
        public TimestampExpression read(Kryo kryo, Input input, Class<TimestampExpression> type) {
            ExpressionOperator operator = ExpressionOperator.operator(kryo.readObject(input, String.class));
            String value = kryo.readObject(input, String.class);
            return new TimestampExpression(operator, value);
        }

    }
}
