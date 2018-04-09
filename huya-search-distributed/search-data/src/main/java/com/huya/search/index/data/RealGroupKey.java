package com.huya.search.index.data;

import com.huya.search.index.meta.IndexFieldType;
import com.huya.search.index.meta.MetaDefine;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/15.
 */
public class RealGroupKey implements GroupKey {

    private Object[] objects;

    private List<IndexableField> groupKeyFields = new ArrayList<>();

    private MetaDefine metaDefine;

    public RealGroupKey(List<String> fields, Iterable<? extends IndexableField> document, MetaDefine metaDefine) {
        this.metaDefine = metaDefine;
        objects = new Object[fields.size()];
        int i = 0;
        for (IndexableField field : document) {
            String name = field.name();
            if (fields.contains(name)) {
                groupKeyFields.add(field);
                objects[i++] = getObjects(name, field);
            }
        }
    }

    private Object getObjects(String field, IndexableField indexableField) {
        IndexFieldType type = metaDefine.getIndexFieldFeatureType(field).getType();
        switch (type) {
            case Integer:
            case Date:
            case Long:
            case Float:
            case Double: return indexableField.numericValue();
            case String:
            case Text: return indexableField.stringValue();
            default: throw new RuntimeException("no support index field type");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealGroupKey that = (RealGroupKey) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(objects, that.objects);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objects);
    }

    @Override
    public List<IndexableField> groupKeyFields() {
        return groupKeyFields;
    }
}
