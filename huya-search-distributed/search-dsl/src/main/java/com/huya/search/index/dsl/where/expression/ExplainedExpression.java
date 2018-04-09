package com.huya.search.index.dsl.where.expression;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.meta.IndexFeatureType;
import com.huya.search.index.meta.MetaDefine;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/28.
 */
public class ExplainedExpression extends BooleanExpression {

    public static ExplainedExpression newInstance(String field, ExpressionOperator operator, String value) {
        return new ExplainedExpression(field, operator, value);
    }

    private ExplainedExpression(String field, ExpressionOperator operator, String value) {
        super(field, operator, value);
    }

    public IndexFeatureType getType(MetaDefine metaDefine) {
        return metaDefine.getIndexFieldFeatureType(getField());
    }

    @Override
    public void explain() {

    }

    public static class ExplainedExpressionSerializer extends Serializer<ExplainedExpression> {

        @Override
        public void write(Kryo kryo, Output output, ExplainedExpression object) {
            kryo.writeObject(output, object.getField());
            kryo.writeObject(output, object.getOperator().toString());
            kryo.writeObject(output, object.getValue());
        }

        @Override
        public ExplainedExpression read(Kryo kryo, Input input, Class<ExplainedExpression> type) {
            String field = kryo.readObject(input, String.class);
            ExpressionOperator operator = ExpressionOperator.operator(kryo.readObject(input, String.class));
            String value = kryo.readObject(input, String.class);
            return new ExplainedExpression(field, operator, value);
        }

    }
}
