package com.huya.facing.convert;

import com.huya.search.facing.convert.BeeLogSvrTafConvert;
import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.index.data.SearchDataRow;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/31.
 */
public class BeeLogSvrTafConvertTest {

    @Test
    public void runConvert() throws IOException, IgnorableConvertException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/geekcat/Documents/data.txt"));
        BeeLogSvrTafConvert convert = new BeeLogSvrTafConvert();
        while (true) {
            String key = bufferedReader.readLine();
            String value = bufferedReader.readLine();

            if (key == null) break;
            try {
                SearchDataRow searchDataRow = convert.convert(0, 0, value, key);
            } catch (IgnorableConvertException ignore) {
                if (!ignore.getCause().getCause().getMessage().contains("商务合作邮箱")) {
                    ignore.printStackTrace();
                }
            }
        }
    }
}
