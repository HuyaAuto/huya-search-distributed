package com.huya.search.index.data.result;

import com.huya.search.index.data.QueryResult;
import org.apache.lucene.index.IndexableField;

import java.util.Iterator;
import java.util.List;

public class StringQueryResult<T  extends Iterable<IndexableField>> extends QueryResult<T> {

    public StringQueryResult(List<T> collection) {
        super();
        add(collection);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (T doc : getCollection()) {
            Iterator<IndexableField> iterator = doc.iterator();
            while (iterator.hasNext()) {
                IndexableField field = iterator.next();
                stringBuilder.append(field.name()).append(" : ").append(field.stringValue());
                if (iterator.hasNext()) stringBuilder.append(",");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
