package com.huya.search.facing.convert;

import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/23.
 */
public class VideoQuality extends AbstractDataConvert<String, String, SearchDataRow> {

    private static final String TIMESTAMP = "its";

    private static final String AREA = "_area";

    private static final String ISP = "_isp";

    @Override
    public SearchDataRow realConvert(int id, long offset, String line, String lineKey) throws IgnorableConvertException {
        List<SearchDataItem> items = new ArrayList<>();
        long unixTime = 0;
        String[] kvs = line.split("\\|");

        for (String kv : kvs) {
            String[] kvArray = kv.split(":");
            if (kvArray.length > 1) {
                String key = kvArray[0];
                String value = kvArray[1];
                if (Objects.equals(TIMESTAMP, key)) {
                    unixTime = Long.parseLong(value) * 1000;
                }
                else {
                    if (Objects.equals(AREA, key) || Objects.equals(ISP, key)) {
                        try {
                            items.add(SearchDataItem.newInstance(key, URLDecoder.decode(value, "UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            items.add(SearchDataItem.newInstance(key, ""));
                        }
                    }
                    else {
                        items.add(SearchDataItem.newInstance(key, value));
                    }
                }
            }
            else if (kvArray.length > 0) {
                items.add(SearchDataItem.newInstance(kvArray[0], ""));
            }
        }

        if (unixTime == 0) throw new IgnorableConvertException("unixTime is zero");

        return new SearchDataRow()
                .setId(id)
                .setOffset(offset)
                .setUnixTime(unixTime)
                .setItems(items);
    }
}
