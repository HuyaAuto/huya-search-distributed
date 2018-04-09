package com.huya.search.facing.convert;

import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/5.
 */
public class HiidoLiveServerWaterConvert extends AbstractDataConvert<String, String, SearchDataRow> {

    private static final String IP = "ip";
    private static final String EVENT = "event";
    private static final String MESSAGE = "message";

    @Override
    protected SearchDataRow realConvert(int id, long offset, String line, String key) throws IgnorableConvertException {
        String[] keyArray = key.split("\\|");

        if (keyArray.length < 5) throw new IgnorableConvertException("key date enough 5");

        String ip = keyArray[3];

        String[] temp = line.split("\u0005", 3);

        if (temp.length < 3) throw new IgnorableConvertException("line date enough 3");

        String timestamp = temp[0];
        String event     = temp[1];
        String message   = temp[2];

        List<SearchDataItem> items = new ArrayList<>();
        items.add(SearchDataItem.newInstance(IP, ip));
        items.add(SearchDataItem.newInstance(EVENT, event));
        items.add(SearchDataItem.newInstance(MESSAGE, message));

        return new SearchDataRow()
                .setId(id)
                .setOffset(offset)
                .setUnixTime(timestamp)
                .setItems(items);
    }
}
