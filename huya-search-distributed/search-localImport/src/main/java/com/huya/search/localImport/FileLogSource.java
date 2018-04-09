package com.huya.search.localImport;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.facing.convert.DataConvert;
import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.index.data.SearchDataRow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
@Singleton
public class FileLogSource implements LogSource {

    private String table;

    private int shardNum;

    private List<BufferedReader> bufferedReaders;

    private long lowUnixTime;

    private long upUnixTime;

    private int currentShardId = 0;

    private int current = 0;

    private BufferedReader currentBufferedReader;

    private DataConvert<String, String, SearchDataRow> dataConvert;

    @Inject
    public FileLogSource(String table, String filesStr, long lowUnixTime, long upUnixTime, int shardNum, String dataConvertClassName) throws FileNotFoundException {
        this.bufferedReaders = initBufferedReaders(filesStr.split(","));
        this.currentBufferedReader = this.bufferedReaders.get(current);
        this.lowUnixTime = lowUnixTime;
        this.upUnixTime = upUnixTime;
        initIndexDeserializer(dataConvertClassName);
        this.table = table;
        this.shardNum = shardNum;
    }

    @SuppressWarnings("unchecked")
    private void initIndexDeserializer(String indexDeserializer) {
        try {
            Class<? extends DataConvert<String, String, SearchDataRow>> clazz =
                    (Class<? extends DataConvert<String, String, SearchDataRow>>) this.getClass().getClassLoader().loadClass(indexDeserializer);
            dataConvert = clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private List<BufferedReader> initBufferedReaders(String[] filesPath) throws FileNotFoundException {
        List<BufferedReader> bufferedReaders = new ArrayList<>();
        for (String filePath : filesPath) {
            bufferedReaders.add(new BufferedReader(new FileReader(new File(filePath))));
        }
        return bufferedReaders;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public boolean hasNext() {
        return current != bufferedReaders.size();
    }

    @Override
    public SearchDataRow next() {
        try {
            String line = this.currentBufferedReader.readLine();
            if (line == null) {
                current ++;
                if (hasNext()) {
                    this.currentBufferedReader = bufferedReaders.get(current);
                }
                return null;
            }
            else {
                return dataConvert.convert(getId(), -1L, line, null);
            }
        } catch (IOException | IgnorableConvertException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getId() {
        return currentShardId ++ % shardNum;
    }
}
