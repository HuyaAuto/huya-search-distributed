package com.huya.search.index.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class LuceneLargeNumberFieldTest {

    private int[] columnArr = new int[]{10, 20, 40, 50, 70, 100, 150, 200, 250, 300};
    private static final int docNum = 1000;

    public void before() {
        for (int i : columnArr) {
            try {
                new File(i + "columns").delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void write() throws Exception {
        before();
        CountDownLatch countDownLatch = new CountDownLatch(columnArr.length);
        for (int columnNum : columnArr) {
            new Thread(() -> {
                File file = new File(columnNum + "columns");
                if (!file.exists()) {
                    file.mkdirs();
                }
                long start = 0;
                long end = 0;
                try {
                    start = System.currentTimeMillis();
                    Directory ramDirectory = writeIndexInRAM(docNum, columnNum);
                    writeIndexInDisk(file.toPath(), ramDirectory);
                    end = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
                System.out.println("写入[" + columnNum + "]耗时:" + (end - start) / 1000);
            }).start();
        }
        countDownLatch.await();

    }

    @Test
    public void read() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(columnArr.length);
        for (int columnNum : columnArr) {
            new Thread(() -> {
                File file = new File(columnNum + "columns");
                if (!file.exists()) {
                    file.mkdirs();
                }
                try {
                    long start = System.currentTimeMillis();
                    readIndex(file.toPath());
                    long end = System.currentTimeMillis();
                    System.out.println("读取[" + columnNum + "]耗时:" + (end - start) / 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }  finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }

    private void readIndex(Path path) throws IOException {
        Directory directory = FSDirectory.open(path);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        IndexReader indexReader = DirectoryReader.open(indexWriter);
        indexReader.docFreq(new Term("column9", "jk9"));
        indexWriter.close();
    }


    private Directory writeIndexInRAM(int docNum, int columnNum) throws Exception {
        Directory directory = new RAMDirectory();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document document = new Document();
        for (int i = 0; i < docNum; i++) {
            for (int j = 0; j < columnNum; j++) {
                document.add(new StringField("column" + j, "jk" + j, Field.Store.YES));
            }
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
        return directory;
    }

    private void writeIndexInDisk(Path path, Directory ramDirectory) throws Exception {
        Directory directory = FSDirectory.open(path);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        indexWriter.addIndexes(ramDirectory);
        indexWriter.commit();
        indexWriter.close();
    }

}
