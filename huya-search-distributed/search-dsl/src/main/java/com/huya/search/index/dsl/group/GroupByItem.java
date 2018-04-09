package com.huya.search.index.dsl.group;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/11.
 */
public interface GroupByItem {

    Collection<String> needExtracted();

    class GroupByItemSerializer extends Serializer<GroupByItem> {

        @Override
        public void write(Kryo kryo, Output output, GroupByItem object) {
            Collection<String> needsExtracted = object.needExtracted();
            kryo.writeObject(output, needsExtracted.size());
            for (String str : needsExtracted) {
                kryo.writeObject(output, str);
            }
        }

        @Override
        public GroupByItem read(Kryo kryo, Input input, Class<GroupByItem> type) {
            int size = kryo.readObject(input, Integer.class);

            List<String> temp = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                temp.add(kryo.readObject(input, String.class));
            }
            return () -> temp;
        }

    }
}
