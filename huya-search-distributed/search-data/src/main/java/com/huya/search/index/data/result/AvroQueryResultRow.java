package com.huya.search.index.data.result;

import com.huya.search.index.data.QueryResultRow;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class AvroQueryResultRow extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord, QueryResultRow {
    private static final long serialVersionUID = -7078243453198078872L;
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroQueryResultRow\",\"namespace\":\"com.huya.search.index.data.result\",\"fields\":[{\"name\":\"result\",\"type\":{\"type\":\"map\",\"values\":\"string\"}}]}");
    public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

    private static SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<AvroQueryResultRow> ENCODER =
            new BinaryMessageEncoder<AvroQueryResultRow>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<AvroQueryResultRow> DECODER =
            new BinaryMessageDecoder<AvroQueryResultRow>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     */
    public static BinaryMessageDecoder<AvroQueryResultRow> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     */
    public static BinaryMessageDecoder<AvroQueryResultRow> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<AvroQueryResultRow>(MODEL$, SCHEMA$, resolver);
    }

    /** Serializes this AvroQueryResultRow to a ByteBuffer. */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /** Deserializes a AvroQueryResultRow from a ByteBuffer. */
    public static AvroQueryResultRow fromByteBuffer(
            java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    @Deprecated public Map<CharSequence,CharSequence> result;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public AvroQueryResultRow() {}

    public AvroQueryResultRow(Iterator<IndexableField> iterator) {
        this.result = new HashMap<>();
        while (iterator.hasNext()) {
            IndexableField field = iterator.next();
            this.result.put(field.name(), field.stringValue());
        }
    }

    /**
     * All-args constructor.
     * @param result The new value for result
     */
    public AvroQueryResultRow(Map<CharSequence,CharSequence> result) {
        this.result = result;
    }

    public org.apache.avro.Schema getSchema() { return SCHEMA$; }
    // Used by DatumWriter.  Applications should not call.
    public Object get(int field$) {
        switch (field$) {
            case 0: return result;
            default: throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value="unchecked")
    public void put(int field$, Object value$) {
        switch (field$) {
            case 0: result = (Map<CharSequence,CharSequence>)value$; break;
            default: throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /**
     * Gets the value of the 'result' field.
     * @return The value of the 'result' field.
     */
    public Map<CharSequence,CharSequence> getResult() {
        return result;
    }

    /**
     * Sets the value of the 'result' field.
     * @param value the value to set.
     */
    public void setResult(Map<CharSequence,CharSequence> value) {
        this.result = value;
    }

    /**
     * Creates a new AvroQueryResultRow RecordBuilder.
     * @return A new AvroQueryResultRow RecordBuilder
     */
    public static AvroQueryResultRow.Builder newBuilder() {
        return new AvroQueryResultRow.Builder();
    }

    /**
     * Creates a new AvroQueryResultRow RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new AvroQueryResultRow RecordBuilder
     */
    public static AvroQueryResultRow.Builder newBuilder(AvroQueryResultRow.Builder other) {
        return new AvroQueryResultRow.Builder(other);
    }

    /**
     * Creates a new AvroQueryResultRow RecordBuilder by copying an existing AvroQueryResultRow instance.
     * @param other The existing instance to copy.
     * @return A new AvroQueryResultRow RecordBuilder
     */
    public static AvroQueryResultRow.Builder newBuilder(AvroQueryResultRow other) {
        return new AvroQueryResultRow.Builder(other);
    }

    /**
     * RecordBuilder for AvroQueryResultRow instances.
     */
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AvroQueryResultRow>
            implements org.apache.avro.data.RecordBuilder<AvroQueryResultRow> {

        private Map<CharSequence,CharSequence> result;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(AvroQueryResultRow.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.result)) {
                this.result = data().deepCopy(fields()[0].schema(), other.result);
                fieldSetFlags()[0] = true;
            }
        }

        /**
         * Creates a Builder by copying an existing AvroQueryResultRow instance
         * @param other The existing instance to copy.
         */
        private Builder(AvroQueryResultRow other) {
            super(SCHEMA$);
            if (isValidValue(fields()[0], other.result)) {
                this.result = data().deepCopy(fields()[0].schema(), other.result);
                fieldSetFlags()[0] = true;
            }
        }

        /**
         * Gets the value of the 'result' field.
         * @return The value.
         */
        public Map<CharSequence,CharSequence> getResult() {
            return result;
        }

        /**
         * Sets the value of the 'result' field.
         * @param value The value of 'result'.
         * @return This builder.
         */
        public AvroQueryResultRow.Builder setResult(Map<CharSequence,CharSequence> value) {
            validate(fields()[0], value);
            this.result = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'result' field has been set.
         * @return True if the 'result' field has been set, false otherwise.
         */
        public boolean hasResult() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'result' field.
         * @return This builder.
         */
        public AvroQueryResultRow.Builder clearResult() {
            result = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public AvroQueryResultRow build() {
            try {
                AvroQueryResultRow record = new AvroQueryResultRow();
                record.result = fieldSetFlags()[0] ? this.result : (Map<CharSequence,CharSequence>) defaultValue(fields()[0]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<AvroQueryResultRow>
            WRITER$ = (org.apache.avro.io.DatumWriter<AvroQueryResultRow>)MODEL$.createDatumWriter(SCHEMA$);

    @Override public void writeExternal(java.io.ObjectOutput out)
            throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<AvroQueryResultRow>
            READER$ = (org.apache.avro.io.DatumReader<AvroQueryResultRow>)MODEL$.createDatumReader(SCHEMA$);

    @Override public void readExternal(java.io.ObjectInput in)
            throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @Override
    public CharSequence getCharSequence(CharSequence name) {
        return result.get(name);
    }

    @Override
    public Iterator<IndexableField> iterator() {
        return new Iterator<IndexableField>(){

            private Iterator<Map.Entry<CharSequence, CharSequence>> iterator = result.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public IndexableField next() {
                Map.Entry<CharSequence, CharSequence> entry = iterator.next();
                return new IndexableField() {

                    @Override
                    public String name() {
                        return entry.getKey().toString();
                    }

                    @Override
                    public IndexableFieldType fieldType() {
                        return null;
                    }

                    @Override
                    public TokenStream tokenStream(Analyzer analyzer, TokenStream reuse) {
                        return null;
                    }

                    @Override
                    public float boost() {
                        return 0;
                    }

                    @Override
                    public BytesRef binaryValue() {
                        return null;
                    }

                    @Override
                    public String stringValue() {
                        return entry.getValue().toString();
                    }

                    @Override
                    public Reader readerValue() {
                        return null;
                    }

                    @Override
                    public Number numericValue() {
                        return null;
                    }
                };
            }
        };
    }
}