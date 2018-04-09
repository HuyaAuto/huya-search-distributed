package com.huya.search.index.data.function;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.meta.IndexFieldType;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/21.
 */
public interface Function {

    FunctionType getFunctionType();

    String functionName();

    IndexFieldType resultIndexFieldType(List<IndexFieldType> attrType);

    class FunctionSerializer extends Serializer<Function> {

        @Override
        public void write(Kryo kryo, Output output, Function object) {
            kryo.writeObject(output, object.functionName());
        }

        @Override
        public Function read(Kryo kryo, Input input, Class<Function> type) {
            String functionName = kryo.readObject(input, String.class);
            return FunctionMap.functionMap.get(functionName);
        }

    }

}
