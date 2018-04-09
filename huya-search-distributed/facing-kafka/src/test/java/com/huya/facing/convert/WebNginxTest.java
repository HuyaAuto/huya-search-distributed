package com.huya.facing.convert;

import com.huya.search.facing.convert.DataConvert;
import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.index.data.SearchDataRow;
import groovy.lang.GroovyCodeSource;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static com.huya.search.facing.subscriber.KafkaShardConsumer.GROOVY_CLASS_LOADER;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/8.
 */
public class WebNginxTest {

    @Test
    public void runConvert() throws IOException, IgnorableConvertException, IllegalAccessException, InstantiationException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/geekcat/Documents/data.txt"));
        Class groovyClass = GROOVY_CLASS_LOADER.parseClass(
                new GroovyCodeSource(Objects.requireNonNull(this.getClass().getClassLoader().getResource("Web_nginxConvert.groovy")))
        );
        DataConvert<String, String, SearchDataRow> convert = (DataConvert<String, String, SearchDataRow>) groovyClass.newInstance();

        while (true) {
            String key = bufferedReader.readLine();
            String value = bufferedReader.readLine();

            if (key == null) break;
            try {
                SearchDataRow searchDataRow = convert.convert(0, 0, value, key);
                System.out.println(searchDataRow);
            } catch (IgnorableConvertException ignore) {

            }
        }
    }


}
