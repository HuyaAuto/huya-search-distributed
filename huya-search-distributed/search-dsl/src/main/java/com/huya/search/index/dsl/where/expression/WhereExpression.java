package com.huya.search.index.dsl.where.expression;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.dsl.where.expression.builder.BuildLuceneQueryException;
import com.huya.search.index.meta.IntactMetaDefine;
import com.huya.search.index.meta.impl.FieldFactory;
import org.apache.lucene.search.Query;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/23.
 */
public interface WhereExpression {

    String getField();

    ExpressionOperator getOperator();

    String getValue();

    void explain();

    boolean aboutTimestamp();

    Query getQuery(IntactMetaDefine intactMetaDefine, String logSvrApp, String logSvrModule, String logSvrType) throws BuildLuceneQueryException;

    class WhereExpressionSerializer extends Serializer<WhereExpression> {

        @Override
        public void write(Kryo kryo, Output output, WhereExpression object) {
            kryo.writeObject(output, object.getField());
            kryo.writeObject(output, object.getOperator().toString());
            kryo.writeObject(output, object.getValue());
        }

        @Override
        public WhereExpression read(Kryo kryo, Input input, Class<WhereExpression> type) {
            String field = kryo.readObject(input, String.class);
            if (!field.equals(FieldFactory.TIME_STAMP)) {
                ExpressionOperator operator = ExpressionOperator.operator(kryo.readObject(input, String.class));
                String value = kryo.readObject(input, String.class);
                return ExplainedExpression.newInstance(field, operator, value);
            }
            else {
                ExpressionOperator operator = ExpressionOperator.operator(kryo.readObject(input, String.class));
                String value = kryo.readObject(input, String.class);
                return TimestampExpression.newInstance(operator, value);
            }
        }

    }
}
