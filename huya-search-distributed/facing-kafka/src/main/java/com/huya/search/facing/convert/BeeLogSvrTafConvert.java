package com.huya.search.facing.convert;

import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/31.
 */
public class BeeLogSvrTafConvert extends AbstractDataConvert<String, String, SearchDataRow> {

    private static final String IP = "ip";
    private static final String FILENAME = "fileName";
    private static final String LINE_NUM = "lineNum";
    private static final String MESSAGE = "message";

    @Override
    protected SearchDataRow realConvert(int id, long offset, String line, String key) throws IgnorableConvertException {
        try {
            String[] keyArray = key.split("\\|");

            if (keyArray.length != 6) throw new IgnorableConvertException("key array is not 6");

            String ip = keyArray[3];

            String[] temp = keyArray[5].split("\u0001");

            if (temp.length != 3) throw new IgnorableConvertException("could not get file name and line num");

            String fileName = temp[0];

            int num = Integer.parseInt(temp[1]);

            String[] valueArray = line.split("\\|", 2);

            String timestamp = valueArray[0];

            String message = valueArray[1];

            List<SearchDataItem> items = new ArrayList<>();
            items.add(SearchDataItem.newInstance(IP, ip));
            items.add(SearchDataItem.newInstance(FILENAME, fileName));
            items.add(SearchDataItem.newInstance(LINE_NUM, num));
            items.add(SearchDataItem.newInstance(MESSAGE, message));

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(timestamp)
                    .setItems(items);
        } catch (Exception e) {
            if (! (e instanceof IgnorableConvertException)) {
                throw new IgnorableConvertException("unknown error", e);
            }
            throw e;
        }
    }
}
