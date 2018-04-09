package com.huya.search.index.data;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapPerformanceTest {

    private static final String[] columns =
            new String[] {"id", "timestamp", "level", "content", "keyWord", "num", "t1", "t2", "t3", "t4"};

    private List<List<IndexableField>> getRandomList(int size) {
        List<List<IndexableField>> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<IndexableField> list = new ArrayList<>();
            for (String str : columns) {
                list.add(new TestIndexableField(str));
            }
            data.add(list);
        }
        return data;
    }

    private List<Map<String, IndexableField>> getRandomMap(int size) {
        List<Map<String, IndexableField>> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Map<String, IndexableField> map = new HashMap<>();
            for (String str : columns) {
                map.put(str, new TestIndexableField(str));
            }
            data.add(map);
        }
        return data;
    }

    public IndexableField getFrom(List<IndexableField> list, String name) {
        for (IndexableField indexableField : list) {
            if (indexableField.name().equals(name)) {
                return indexableField;
            }
        }
        return null;
    }

    @Test
    public void runListTest() {
        long start = System.currentTimeMillis();
        List<List<IndexableField>> data = getRandomList(100000);
        for (List<IndexableField> list : data) {
            for (String str : columns) {
                IndexableField indexableField = getFrom(list, str);
            }
        }
        long end   = System.currentTimeMillis();
        System.out.println(end - start + " ms");
    }

    @Test
    public void runMapTest() {
        long start = System.currentTimeMillis();
        List<Map<String, IndexableField>> data = getRandomMap(100000);
        for (Map<String, IndexableField> map : data) {
            for (String str : columns) {
                IndexableField indexableField = map.get(str);
            }
        }
        long end   = System.currentTimeMillis();
        System.out.println(end - start + " ms");
    }


    class TestIndexableField implements IndexableField {

        private String name;

        public TestIndexableField(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
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
            return name;
        }

        @Override
        public Reader readerValue() {
            return null;
        }

        @Override
        public Number numericValue() {
            return null;
        }
    }
}
