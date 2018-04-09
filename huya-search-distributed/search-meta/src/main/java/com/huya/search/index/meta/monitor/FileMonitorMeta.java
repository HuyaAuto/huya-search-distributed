package com.huya.search.index.meta.monitor;

import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.MetaOperatorException;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.util.PathUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/20.
 */
public class FileMonitorMeta extends MonitorOriginalMeta {

    private static final String FILE_SUFFIX        = ".meta";
    private static final String REMOVE_FILE_SUFFIX = ".remove";

    public static FileMonitorMeta newInstance(String path) {
        return new FileMonitorMeta(path);
    }

    private String path;

    private FileMonitorMeta(String path) {
        this.path = path;
    }

    private String filePath(String table) {
        return path + PathUtil.separator + table;
    }

    @Override
    public String tag() {
        return "path:" + path;
    }

    @Override
    protected void addSelf(TimelineMetaDefine metaDefine) {
        updateSelf(metaDefine);
    }

    @Override
    protected void updateSelf(String table, MetaDefine metaDefine) {
        TimelineMetaDefine oldMetaDefine = getTimelineMetaDefine(table);
        oldMetaDefine.add(metaDefine);
        updateSelf(oldMetaDefine);
    }

    @Override
    protected void updateSelf(TimelineMetaDefine metaDefine) {
        String table = metaDefine.getTable();
        try {
            FileWriter fileWriter = new FileWriter(filePath(table));
            fileWriter.write(metaDefine.toObject().toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void removeSelf(String table) {
        File file = new File(filePath(table));
        if (file.exists() && file.isFile()) {
            boolean success = file.renameTo(getRenameFile(table));
            if (!success) throw new MetaOperatorException("remove fail, file rename fail");
        }
    }

    private File getRenameFile(String table) {
        File file = new File(filePath(table) + REMOVE_FILE_SUFFIX);
        int i = 1;
        while (file.exists()) {
            file = new File(filePath(table) + REMOVE_FILE_SUFFIX + i ++);
        }
        return file;
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        File dir = new File(path);
        assert dir.exists() && dir.isDirectory();
        File[] files = dir.listFiles();
        assert files != null;
        Collection<TimelineMetaDefine> collection = new ArrayList<>();
        for (File metaFile : files) {
            if (metaFile.getName().endsWith(FILE_SUFFIX)) {
                String content = getContentFromFile(metaFile);
                collection.add(JsonMetaUtil.createTimelineMetaDefineFromStr(content));
            }
        }
        return collection.iterator();
    }

    @Override
    public TimelineMetaDefine getTimelineMetaDefine(String table) {
        File file = new File(filePath(table));
        assert file.exists() && file.isFile();
        if (file.getName().endsWith(FILE_SUFFIX)) {
            String content = getContentFromFile(file);
            return JsonMetaUtil.createTimelineMetaDefineFromStr(content);
        }
        return null;
    }

    private String getContentFromFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                stringBuilder.append(tempString);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    @Override
    public void unload() {
        //do nothing
    }
}
