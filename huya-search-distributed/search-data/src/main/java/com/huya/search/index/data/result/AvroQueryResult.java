package com.huya.search.index.data.result;

import com.huya.search.index.data.QueryResult;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AvroQueryResult extends QueryResult<AvroQueryResultRow> {

    public AvroQueryResult(List<AvroQueryResultRow> collection) {
        super(collection);
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<AvroQueryResultRow> writer = new SpecificDatumWriter<>(AvroQueryResultRow.getClassSchema());
        try {
            for (AvroQueryResultRow row : getCollection()) {
                writer.write(row, encoder);
            }
            encoder.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}
