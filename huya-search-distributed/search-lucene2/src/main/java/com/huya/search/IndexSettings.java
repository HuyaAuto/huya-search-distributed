package com.huya.search;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.index.analyzer.DynamicAnalyzer;
import com.huya.search.settings.ImmutableSettings;
import com.huya.search.util.FileAlterationUtil;
import com.huya.search.util.PropertiesUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/14.
 */
@Singleton
public class IndexSettings extends ImmutableSettings {

    private static final Logger LOG = LoggerFactory.getLogger(IndexSettings.class);

    private static   Map<String, Object> initSettings;
    private static  Map<String, DynamicAnalyzer> dynamicAnalyzerSettings;

    static {
        Properties properties = PropertiesUtils.getProperties("settings.properties");
        initSettings = new HashMap<>();
        assert properties != null;
        for (String key : properties.stringPropertyNames()) {
            initSettings.put(key, properties.getProperty(key));
        }

        dynamicAnalyzerSettings = Maps.newHashMap();
        try {
            properties = PropertiesUtils.getProperties("dynamic-analyzer.properties");
            StringBuilder sb = new StringBuilder();
            sb.append("dynamic-analyzer.properties.size:" + properties.size());
            assert properties != null;
            for (String key : properties.stringPropertyNames()) {
                String[] keyArr = key.split("-");
                sb.append("加载[dynamic-analyzer.properties]成功." + ", key:" + key + ", keyArr:" + ArrayUtils.toString(keyArr) + ", value:" + properties.getProperty(key));
                dynamicAnalyzerSettings.put(key, DynamicAnalyzer.getInstance(keyArr[0], keyArr[1], keyArr[2], properties.getProperty(key)));
            }
            LOG.info(sb.toString());

            new Thread(() -> {
                try {
                    FileAlterationUtil.monitorFileChange("dynamic-analyzer.properties", () -> {
                        Properties prop = PropertiesUtils.getProperties("dynamic-analyzer.properties");
                        assert prop != null;
                        for (String key : prop.stringPropertyNames()) {
                            String[] keyArr = key.split("-");
                            dynamicAnalyzerSettings.put(key, DynamicAnalyzer.getInstance(keyArr[0], keyArr[1], keyArr[2], prop.getProperty(key)));
                        }
                        LOG.info("刷新配置文件成功[dynamic-analyzer.properties]!");
                    });
                } catch (Exception e) {
                    LOG.error("监听dynamic-analyzer.properties发生错误:" + e.getMessage(), e);
                }
            }).start();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static DynamicAnalyzer getDynamicAnalyzer(String app, String module, String type) {
        DynamicAnalyzer dynamicAnalyzer = dynamicAnalyzerSettings.get(app + "-" + module + "-" + type);
        if (dynamicAnalyzer == null) {
            dynamicAnalyzer = dynamicAnalyzerSettings.get(app+ "-" +module+ "-" +"default");
        }
        if (dynamicAnalyzer == null) {
            dynamicAnalyzer = dynamicAnalyzerSettings.get(app+ "-" + "default"+ "-" + "default");
        }
        return dynamicAnalyzer;
    }

    @Inject
    IndexSettings() {
        super(initSettings, IndexSettings.class.getClassLoader());
    }
}