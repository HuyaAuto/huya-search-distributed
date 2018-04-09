package com.huya.search.index.dsl.limit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/7.
 */
public abstract class LimitExpr {

    public abstract int to();

    public abstract int from();

    public int size() {
        return to() - from() + 1;
    }

    public abstract boolean selectAll();

    public int top() {
        return to() + 1;
    }

    public static class LimitExprSerializer extends Serializer<LimitExpr> {

        @Override
        public void write(Kryo kryo, Output output, LimitExpr object) {
            kryo.writeObject(output, object.to());
            kryo.writeObject(output, object.from());
            kryo.writeObject(output, object.selectAll());
        }

        @Override
        public LimitExpr read(Kryo kryo, Input input, Class<LimitExpr> type) {
            int to = kryo.readObject(input, Integer.class);
            int from = kryo.readObject(input, Integer.class);
            boolean selectAll = kryo.readObject(input, Boolean.class);

            return new LimitExpr() {

                @Override
                public int to() {
                    return to;
                }

                @Override
                public int from() {
                    return from;
                }

                @Override
                public boolean selectAll() {
                    return selectAll;
                }
            };
        }

    }
}
