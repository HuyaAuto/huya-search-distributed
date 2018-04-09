package com.huya.search.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * Created by geekcat on 2017/7/1.
 */
public class PropertiesUtils {

    private static Logger LOG = LoggerFactory.getLogger(PropertiesUtils.class);

    public static Properties getProperties(String fileName) {
        InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            Properties pros = new Properties();
            pros.load(new InputStreamReader(is, "UTF-8"));
            return pros;
        } catch (Exception e) {
            LOG.error("load properties err, filename :{}", fileName, e);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {}
        }
        return null;
    }

    /**
     * get object by key
     *
     * @param key
     * @return object
     */
    public static String getByKey(Properties pros, String key) {
        if (null != pros) {
            Object obj = pros.get(key);
            if (null != obj) {
                return obj.toString();
            }
        }
        return null;
    }
}
