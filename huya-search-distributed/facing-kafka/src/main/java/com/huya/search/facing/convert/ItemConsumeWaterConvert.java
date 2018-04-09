package com.huya.search.facing.convert;

import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/28.
 */
public class ItemConsumeWaterConvert extends AbstractDataConvert<String, String, SearchDataRow> {

    private static final String IP = "ip";

    private static final String ITEM_CONSUME_WATER = "item_consume_water";

    @Override
    public SearchDataRow realConvert(int id, long offset, String line, String key) throws IgnorableConvertException {
        String [] temp = line.split("\\|");
        if (temp.length == 2) {
            List<SearchDataItem> items = new ArrayList<>();
            items.add(SearchDataItem.newInstance(IP, temp[0]));
            String [] strItems = temp[1].split(",");
            long timestamp;
            if (strItems.length >= 2) {
                timestamp = Long.parseLong(strItems[1]) * 1000;

                items.add(SearchDataItem.newInstance(ITEM_CONSUME_WATER, temp[1]));

                return new SearchDataRow()
                        .setId(id)
                        .setOffset(offset)
                        .setUnixTime(timestamp)
                        .setItems(items);
            }
        }
        throw new IgnorableConvertException("unParse log");
    }
}
