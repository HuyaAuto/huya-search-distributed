package com.huya.search.index.dsl.select;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.meta.IndexFieldType;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/11.
 */
public interface SelectItem extends AsField, Alias, Expr, ResultType {

    int number();


    class SelectItemSerializer extends Serializer<SelectItem> {

        @Override
        public void write(Kryo kryo, Output output, SelectItem object) {
            kryo.writeObject(output, object.getField());
            kryo.writeObjectOrNull(output, object.getAlias(), String.class);
            kryo.writeObject(output, object.getExpr());
            kryo.writeObject(output, object.type().toString());
            kryo.writeObject(output, object.number());
        }

        @Override
        public SelectItem read(Kryo kryo, Input input, Class<SelectItem> type) {
            String field = kryo.readObject(input, String.class);
            String alias = kryo.readObjectOrNull(input, String.class);
            String expr  = kryo.readObject(input, String.class);
            IndexFieldType indexFieldType = IndexFieldType.getType(kryo.readObject(input, String.class));
            int num      = kryo.readObject(input, Integer.class);
            return AntrlColumnSelectItem.newInstance(field, alias, expr, indexFieldType, num);
        }

    }
}
