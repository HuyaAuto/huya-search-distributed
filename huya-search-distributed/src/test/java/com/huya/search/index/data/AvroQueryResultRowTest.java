package com.huya.search.index.data;

import com.huya.search.index.data.result.AvroQueryResultRow;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.lucene.index.IndexableField;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AvroQueryResultRowTest {

    private static final Map<CharSequence, CharSequence> map = new HashMap<>();

    static {
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
    }

    @Test
    public void runTest() throws IOException {
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File file = new File(relPath + "../AvroQueryResultRow.avro");
        AvroQueryResultRow result = new AvroQueryResultRow(map);
        DatumWriter<AvroQueryResultRow> writer = new SpecificDatumWriter<>(AvroQueryResultRow.class);
        DataFileWriter<AvroQueryResultRow> dataFileWriter = new DataFileWriter<>(writer);
        dataFileWriter.create(AvroQueryResultRow.getClassSchema(), file);
        dataFileWriter.append(result);
        dataFileWriter.close();

        DatumReader<AvroQueryResultRow> userDatumReader = new SpecificDatumReader<>(AvroQueryResultRow.class);
        DataFileReader<AvroQueryResultRow> dataFileReader = new DataFileReader<>(file, userDatumReader);
        while (dataFileReader.hasNext()) {
            AvroQueryResultRow temp = dataFileReader.next();

            Assert.assertEquals(temp.getResult().size(), 3);

            for (IndexableField field : temp) {
                Assert.assertEquals(map.get(field.name()), field.stringValue());
            }
        }
    }
}
