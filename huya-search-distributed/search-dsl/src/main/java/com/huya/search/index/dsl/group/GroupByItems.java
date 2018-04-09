package com.huya.search.index.dsl.group;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huya.search.index.data.RealGroupKey;
import com.huya.search.index.data.GroupKey;
import com.huya.search.index.meta.MetaService;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.inject.ModulesBuilder;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public abstract class GroupByItems {

    private List<GroupByItem> itemList = new ArrayList<>();

    private String table;

    public GroupByItems(String table) {
        this.table = table;
    }

    public List<String> groupFields() {
        List<String> lazyGroupFields = new ArrayList<>();
        for (GroupByItem item : itemList) {
            lazyGroupFields.addAll(item.needExtracted());
        }
        return lazyGroupFields;
    }

    public String getTable() {
        return table;
    }

    public void addGroupItem(GroupByItem item) {
        itemList.add(item);
    }

    public abstract GroupKey key(Iterable<IndexableField> document);

    public static class GroupByItemsSerializer extends Serializer<GroupByItems> {

        @Override
        public void write(Kryo kryo, Output output, GroupByItems object) {
            kryo.writeObject(output, object.table);
            int size = object.itemList.size();
            kryo.writeObject(output, size);
            for (GroupByItem groupByItem : object.itemList) {
                kryo.writeObject(output, groupByItem);
            }
        }

        @Override
        public GroupByItems read(Kryo kryo, Input input, Class<GroupByItems> type) {
            String table = kryo.readObject(input, String.class);
            int size = kryo.readObject(input, Integer.class);

            TimelineMetaDefine metaDefine = ModulesBuilder.getInstance()
                    .createInjector().getInstance(MetaService.class).get(table);

            GroupByItems groupByItems = new GroupByItems(metaDefine.getTable()) {

                @Override
                public GroupKey key(Iterable<IndexableField> document) {
                    return new RealGroupKey(groupFields(), document, metaDefine);
                }
            };

            for (int i = 0; i < size; i++) {
                groupByItems.addGroupItem(kryo.readObject(input, GroupByItem.class));
            }

            return groupByItems;
        }

    }
}
