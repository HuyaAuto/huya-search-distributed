package com.huya.search.index.dsl.where;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.constant.LogSvrConstants;
import com.huya.search.index.dsl.where.expression.TimestampExpression;
import com.huya.search.index.dsl.where.expression.WhereExpression;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.partition.PartitionRange;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.Map;


/**
 * Created by zhangyiqun1@yy.com on 2017/8/23.
 */
public class SingleWhereStatement implements WhereStatement {

    private WhereExpression expression;

    public static WhereStatement create(WhereExpression expression) {
        return new SingleWhereStatement(expression);
    }

    private SingleWhereStatement(WhereExpression expression) {
        this.expression = expression;
    }

    public WhereExpression getExpression() {
        return expression;
    }

    @Override
    public WhereStatementType type() {
        return WhereStatementType.SINGLE;
    }

    @Override
    public PartitionRange getPartitionRange() {
        if (expression.aboutTimestamp()) {
            return ((TimestampExpression)expression).getPartitionRange();
        }
        return PartitionRange.ALL;
    }

    @Override
    public String getLogSvrApp() {
        if (expression.getField().equals(LogSvrConstants.__APP__)) {
            return expression.getValue();
        }
        return null;
    }

    @Override
    public String getLogSvrModule() {
        if (expression.getField().equals(LogSvrConstants.__MODULE__)) {
            return expression.getValue();
        }
        return null;
    }

    @Override
    public String getLogSvrType() {
        if (expression.getField().equals(LogSvrConstants.__TYPE__)) {
            return expression.getValue();
        }
        return null;
    }

    @Override
    public BooleanQuery getQuery(IntactMetaDefine intactMetaDefine) throws BuildLuceneQueryException {
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(expression.getQuery(intactMetaDefine, null, null, null), BooleanClause.Occur.FILTER);
        return bqb.build();
    }

    @Override
    public BooleanQuery getQuery(
            IntactMetaDefine intactMetaDefine,
            String logSvrApp,
            String logSvrModule,
            String logSvrType
            ) throws BuildLuceneQueryException {
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(expression.getQuery(intactMetaDefine, logSvrApp, logSvrModule, logSvrType), BooleanClause.Occur.FILTER);
        return bqb.build();
    }

    public static class SingleWhereStatementSerializer extends Serializer<SingleWhereStatement> {

        @Override
        public void write(Kryo kryo, Output output, SingleWhereStatement object) {
            kryo.writeObject(output, object.expression);
        }

        @Override
        public SingleWhereStatement read(Kryo kryo, Input input, Class<SingleWhereStatement> type) {
            WhereExpression whereExpression = kryo.readObject(input, WhereExpression.class);
            return new SingleWhereStatement(whereExpression);
        }

    }
}
