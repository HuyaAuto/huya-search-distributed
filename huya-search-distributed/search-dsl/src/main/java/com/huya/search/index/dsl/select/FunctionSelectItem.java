package com.huya.search.index.dsl.select;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.function.Function;
import com.huya.search.index.meta.IndexFieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/21.
 */
public interface FunctionSelectItem extends SelectItem {

    Function getFunction();

    List<SelectItem> getAttrColumn();

    class FunctionSelectItemSerializer extends Serializer<FunctionSelectItem> {

        @Override
        public void write(Kryo kryo, Output output, FunctionSelectItem object) {
            kryo.writeObject(output, object.getField());
            kryo.writeObjectOrNull(output, object.getAlias(), String.class);
            kryo.writeObject(output, object.getExpr());
            kryo.writeObject(output, object.type().toString());
            kryo.writeObject(output, object.number());
            kryo.writeObject(output, object.getFunction());

            int attrSize = object.getAttrColumn().size();
            kryo.writeObject(output, attrSize);

            object.getAttrColumn().forEach(selectItem -> kryo.writeObject(output, selectItem));
        }

        @Override
        public FunctionSelectItem read(Kryo kryo, Input input, Class<FunctionSelectItem> type) {
            String field = kryo.readObject(input, String.class);
            String alias = kryo.readObjectOrNull(input, String.class);
            String expr  = kryo.readObject(input, String.class);
            IndexFieldType indexFieldType = IndexFieldType.getType(kryo.readObject(input, String.class));
            int num      = kryo.readObject(input, Integer.class);
            Function function = kryo.readObject(input, Function.class);
            int attrSize = kryo.readObject(input, Integer.class);

            List<SelectItem> temp = new ArrayList<>();

            for (int i = 0; i < attrSize; i++) {
                temp.add(kryo.readObject(input, SelectItem.class));
            }

            return AntrlFunctionSelectItem.newInstance(field, alias, expr, indexFieldType, num, function, temp);
        }

    }

}
