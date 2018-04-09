package com.huya.facing.convert;

import com.huya.search.facing.convert.IgnorableConvertException;
import com.huya.search.facing.convert.OpenfalconConvert;
import com.huya.search.index.data.SearchDataRow;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/27.
 */
public class OpenfalconConvertTest {

    @Test
    public void runTest() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/geekcat/Documents/data.txt"));
        OpenfalconConvert convert = new OpenfalconConvert();
        while (true) {
            String key = bufferedReader.readLine();
            String value = bufferedReader.readLine();
            if (key == null) break;
            try {
                SearchDataRow searchDataRow = convert.convert(0, 0, value, null);
                System.out.println(searchDataRow.toString());
            } catch (IgnorableConvertException ignore) {
                ignore.printStackTrace();
            }
        }
    }

}
