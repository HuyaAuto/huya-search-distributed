package com.huya.search.index.dsl.sorted;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.lucene.search.SortField;

public interface SortedItem {

    SortField getSortField();


    class SortedItemSerializer extends Serializer<SortedItem> {

        @Override
        public void write(Kryo kryo, Output output, SortedItem object) {
            SortField sortField = object.getSortField();
            String field = sortField.getField();
            SortField.Type type = sortField.getType();
            boolean reverse = sortField.getReverse();

            kryo.writeObject(output, field);
            kryo.writeObject(output, type);
            kryo.writeObject(output, reverse);
        }

        @Override
        public SortedItem read(Kryo kryo, Input input, Class<SortedItem> type) {
            String field = kryo.readObject(input, String.class);
            SortField.Type sortFieldType = kryo.readObject(input, SortField.Type.class);
            boolean reverse = kryo.readObject(input, Boolean.class);

            return () -> new SortField(field, sortFieldType, reverse);
        }

    }
}
