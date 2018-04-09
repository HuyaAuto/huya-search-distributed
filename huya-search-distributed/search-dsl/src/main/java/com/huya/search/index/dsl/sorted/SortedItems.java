package com.huya.search.index.dsl.sorted;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.ArrayList;
import java.util.List;

import static com.huya.search.index.dsl.sorted.AntrlSortedItem.TIMESTAMP_DESC;

/**
 * Created by zhangyiqun1@yy.com on 2017/9/5.
 */
public abstract class SortedItems {

    public static final String NEW_DOC = "new_doc";

    private List<SortedItem> itemList = new ArrayList<>();

    private Sort sort;

    private boolean newDoc = false;

    public Sort sortInstance() {
        if (sort == null) {
            if (onlySortByNewDoc()) {
                SortField[] sortFields = {TIMESTAMP_DESC};
                sort = new Sort(sortFields);
            }
            else {
                SortField[] sortFields = new SortField[itemList.size()];
                for (int i = 0; i < itemList.size(); i++) {
                    sortFields[i] = itemList.get(i).getSortField();
                }
                sort = new Sort(sortFields);
            }
        }
        return sort;
    }

    public void addSortedItem(SortedItem sortedItem) {
        itemList.add(sortedItem);
    }

    public void setNewDoc(boolean newDoc) {
        this.newDoc = newDoc;
    }

    public boolean isNewDoc() {
        return newDoc;
    }

    public boolean onlySortByNewDoc() {
        return newDoc && itemList.size() == 0;
    }

    public boolean mixSortByNewDoc() {
        return newDoc && itemList.size() > 0;
    }

    public static class SortedItemsSerializer extends Serializer<SortedItems> {

        @Override
        public void write(Kryo kryo, Output output, SortedItems object) {
            int size = object.itemList.size();
            kryo.writeObject(output, size);
            for (SortedItem sortedItem : object.itemList) {
                kryo.writeObject(output, sortedItem);
            }
            kryo.writeObject(output, object.isNewDoc());
        }

        @Override
        public SortedItems read(Kryo kryo, Input input, Class<SortedItems> type) {
            int size = kryo.readObject(input, Integer.class);

            SortedItems sortedItems = new SortedItems() {};
            for (int i = 0; i < size; i++) {
                sortedItems.addSortedItem(kryo.readObject(input, SortedItem.class));
            }
            sortedItems.setNewDoc(kryo.readObject(input, Boolean.class));
            return sortedItems;
        }

    }

}
