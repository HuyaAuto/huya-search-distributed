package com.huya.search.index.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.data.util.DataConvert;
import com.huya.search.index.meta.*;
import com.huya.search.index.meta.impl.FieldFactory;
import com.huya.search.util.JodaUtil;
import org.apache.lucene.index.IndexableField;

import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public class SearchDataRow implements Iterable<IndexableField> {

    private int  id;
    private long offset;
    private long unixTime;

    private List<SearchDataItem> items;

    public static SearchDataRow buildFromJson(int id, long offset, ObjectNode objectNode) {
        SearchDataRow searchDataRow = new SearchDataRow();
        List<SearchDataItem> items = new ArrayList<>();
        final long[] timestamp = {-1};
        objectNode.fields().forEachRemaining((entry) -> {
            if (Objects.equals(MetaEnum.TIMESTAMP, entry.getKey())) {
                if (entry.getValue().isNumber()) {
                    timestamp[0] = entry.getValue().longValue() * 1000;
                }
                else {
                    timestamp[0] = Long.parseLong(entry.getValue().textValue()) * 1000;
                }
            }
            else {
                JsonNode jsonNode = entry.getValue();
                Object obj;
                if (jsonNode.isObject() || jsonNode.isArray()) {
                    obj = jsonNode.toString();
                }
                else {
                    obj = jsonNode.isNumber()
                            ? DataConvert.jsonNodeNumberToJavaNumber(jsonNode)
                            : jsonNode.textValue();
                }
                items.add(SearchDataItem.newInstance(entry.getKey(), obj));
            }
        });

        if (timestamp[0] == -1) throw new RuntimeException("no exist timestamp fields");

        return searchDataRow.setId(id)
                .setOffset(offset)
                .setUnixTime(timestamp[0])
                .setItems(items);
    }

    public int getId() {
        return id;
    }

    public long getOffset() {
        return offset;
    }

    public SearchDataRow setId(int id) {
        this.id = id;
        return this;
    }

    public SearchDataRow setOffset(long offset) {
        this.offset = offset;
        return this;
    }

    public long getUnixTime() {
        return unixTime;
    }


    public SearchDataRow setUnixTime(String timestamp) {
        this.unixTime = JodaUtil.getUnixTime(timestamp);
        return this;
    }

    public SearchDataRow setUnixTime(long unixTime) {
        this.unixTime = unixTime;
        return this;
    }

    public List<SearchDataItem> getItems() {
        return items;
    }

    public SearchDataRow setItems(List<SearchDataItem> items) {
        this.items = items;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        items.forEach(searchDataItem -> sb.append(searchDataItem.toString()).append(" "));

        return "SearchDataRow{" +
                "id='" + id + '\'' +
                "offset='" + offset + '\'' +
                ", unixTime='" + unixTime + '\'' +
                ", items={" + sb.toString() +
                "}}";
    }

    public Iterator<IndexField> iterator(MetaDefine metaDefine) {
        return new SearchDataRowIterator(metaDefine);
    }

    @Override
    public Iterator<IndexableField> iterator() {
        return new SearchDataIndexableRowIterator();
    }

    public class SearchDataIndexableRowIterator implements Iterator<IndexableField> {

        private Iterator<SearchDataItem> iterator;

        private boolean hasId = true;

        private boolean hasOffset = true;

        private boolean hasUnixTime = true;

        SearchDataIndexableRowIterator() {
            this.iterator = items.iterator();
        }


        @Override
        public boolean hasNext() {
            return hasId || hasOffset || hasUnixTime || iterator.hasNext();
        }


        @Override
        public IndexableField next() {
            if (hasId) {
                hasId = false;
                return FieldFactory.createIndexableField(FieldFactory.ID, id);
            }
            else if (hasOffset) {
                hasOffset = false;
                return FieldFactory.createIndexableField(FieldFactory.OFFSET, offset);
            }
            else if (hasUnixTime) {
                hasUnixTime = false;
                return FieldFactory.createIndexableField(FieldFactory.TIME_STAMP, unixTime);
            }

            SearchDataItem item = iterator.next();

            String name = item.getKey();

            Object object = item.getValue();
            return FieldFactory.createIndexableField(name, object);
        }
    }

    public class SearchDataRowIterator implements Iterator<IndexField> {

        private MetaDefine metaDefine;

        private Iterator<SearchDataItem> iterator;

        private boolean hasId = true;

        private boolean hasOffset = true;

        private boolean hasUnixTime = true;

        SearchDataRowIterator(MetaDefine metaDefine) {
            this.metaDefine = metaDefine;
            this.iterator = items.iterator();
        }

        @Override
        public boolean hasNext() {
            return hasId || hasOffset || hasUnixTime || iterator.hasNext();
        }

        @Override
        public IndexField next() {
            if (hasId) {
                hasId = false;
                return FieldFactory.createIdIndexField(id);
            }
            else if (hasOffset) {
                hasOffset = false;
                return FieldFactory.createOffsetIndexField(offset);
            }
            else if (hasUnixTime) {
                hasUnixTime = false;
                return FieldFactory.createTimestampField(unixTime);
            }

            SearchDataItem item = iterator.next();

            String name = item.getKey();

            Object object = item.getValue();

            IndexFeatureType indexFeatureType = metaDefine.getIndexFieldFeatureType(item.getKey());
            if (item.getAnalyzer() != null) {
                indexFeatureType.setAnalyzer(item.getAnalyzer());
            }
            return FieldFactory.createIndexField(name, object, indexFeatureType);
        }
    }
}
