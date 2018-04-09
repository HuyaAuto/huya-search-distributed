package com.huya.search.index.data.result;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.huya.search.index.data.QueryResult;
import org.apache.lucene.index.IndexableField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class JsonQueryResult<T extends Iterable<IndexableField>> extends QueryResult<T> {

    public JsonQueryResult(QueryResult<T> queryResult) {
        super();
        add(queryResult.getCollection());
    }

    public JsonQueryResult(List<T> documents) {
        super();
        add(documents);
    }

    //todo 去除这个多余的类
    @Override
    public String toString() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonFactory factory = new JsonFactory();
        try {
            JsonGenerator jGenerator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
            jGenerator.writeStartArray();
            for (T doc : getCollection()) {
                Iterator<IndexableField> iterator = doc.iterator();
                jGenerator.writeStartObject();
                while (iterator.hasNext()) {
                    IndexableField field = iterator.next();
                    jGenerator.writeStringField(field.name(), field.stringValue());
                }
                jGenerator.writeEndObject();
            }
            jGenerator.writeEndArray();
            jGenerator.flush();
            jGenerator.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }


}
