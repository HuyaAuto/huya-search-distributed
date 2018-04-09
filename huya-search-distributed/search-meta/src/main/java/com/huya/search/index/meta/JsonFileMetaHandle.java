package com.huya.search.index.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.huya.search.settings.Settings;
import com.huya.search.util.PathUtil;

import java.io.*;

@Singleton
public class JsonFileMetaHandle implements MetaHandle {

    private static final String META_PATH          = "meta.path";
    private static final String FILE_SUFFIX        = ".meta";
    private static final String REMOVE_FILE_SUFFIX = ".remove";

    private Settings settings;

    private JsonMetaHandle jsonMetaHandle = new JsonMetaHandle();

    @Inject
    public JsonFileMetaHandle(@Named("Meta") Settings settings) {
        this.settings = settings;
    }

    @Override
    public MetaCollection load() {
        String path = settings.get(META_PATH);
        File dir = new File(path);
        assert dir.exists() && dir.isDirectory();
        File[] files = dir.listFiles();
        assert files != null;
        for (File metaFile : files) {
            if (metaFile.getName().endsWith(FILE_SUFFIX)) {
                String content = getContentFromFile(metaFile);
                String table = getTableNameFromFileName(metaFile.getName());
                jsonMetaHandle.putJson(table, content);
            }
        }

        return jsonMetaHandle.load();
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
    public void unLoad() {
        //do nothing
    }

    @Override
    public TimelineMetaDefine createTimelineMetaDefine(ObjectNode objectNode) {
        return jsonMetaHandle.createTimelineMetaDefine(objectNode);
    }

    @Override
    public MetaDefine createMetaDefine(TimelineMetaDefine metaDefine, ObjectNode objectNode) {
        return jsonMetaHandle.createMetaDefine(metaDefine, objectNode);
    }

    @Override
    public void persistence(TimelineMetaDefine metaDefine) {
        jsonMetaHandle.persistence(metaDefine);
        String path = settings.get(META_PATH);
        ObjectNode objectNode = metaDefine.toObject();
        String table = objectNode.get(MetaEnum.TABLE).textValue();
        File file = new File(path + PathUtil.separator + fileName(table));
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonMetaHandle.getJson(table));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new MetaOperatorException("persistence fail, io error", e);
        }
    }

    @Override
    public void remove(String table) {
        jsonMetaHandle.remove(table);
        String path = settings.get(META_PATH);
        File file = new File(path + PathUtil.separator + fileName(table));
        if (!file.exists()) throw new MetaOperatorException("remove fail, file not exist");
        File renameFile = getRenameFile(table);
        boolean success = file.renameTo(renameFile);
        if (!success) throw new MetaOperatorException("remove fail, file rename fail");
    }

    @Override
    public void update(String table, MetaDefine metaDefine) {
        throw new MetaOperatorException("json file meta handle is not support update metaDefine");
    }

    private File getRenameFile(String table) {
        String path = settings.get(META_PATH);
        File file = new File(path + PathUtil.separator + fileName(table) + REMOVE_FILE_SUFFIX);
        int i = 1;
        while (file.exists()) {
            file = new File(path + PathUtil.separator + fileName(table) + REMOVE_FILE_SUFFIX + i ++);
        }
        return file;
    }

    private String fileName(String table) {
        return table + FILE_SUFFIX;
    }

    private String getTableNameFromFileName(String fileName) {
        assert fileName != null;
        String[] temp = fileName.split("\\.");
        assert temp.length > 0;
        return temp[0];
    }
}
