package com.huya.search.facing.convert;

import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/28.
 */
public class CardPackageConvert extends AbstractDataConvert<String, String, SearchDataRow> {

    private static final String IP = "ip";

    private static final String UN_PARSE = "unParse";

    @Override
    public SearchDataRow realConvert(int id, long offset, String line, String key) throws IgnorableConvertException {
        String [] temp = line.split("\\|");
        if (temp.length > 2) {
            List<SearchDataItem> items = new ArrayList<>();
            items.add(SearchDataItem.newInstance(IP, temp[0]));
            String timestamp = temp[1];

            List<String> unParseList = new ArrayList<>();

            for (int i = 2; i < temp.length; i++) {
                int num = StringUtils.countMatches(temp[i], ":");

                if (num == 1) {
                    addKV(temp[i], items, unParseList);
                } else if (num == 2) {
                    String kvs[] = temp[i].split(" ", 2);
                    for (String kv : kvs) {
                        addKV(kv, items, unParseList);
                    }
                }
            }
            if (unParseList.size() > 0) {
                items.add(SearchDataItem.newInstance(UN_PARSE, StringUtils.join(unParseList, "|")));
            }

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(timestamp)
                    .setItems(items);

        }
        else {
            throw new IgnorableConvertException("data not enough");
        }
    }

    private String convertKey(String key) {
        return key.trim().replaceAll(" ", "_");
    }

    private void addKV(String temp, List<SearchDataItem> items, List<String> unParseList) {
        String[] kv = temp.split(":");
        if (kv.length == 2) {
            items.add(SearchDataItem.newInstance(convertKey(kv[0]), kv[1]));
        }
        else {
            unParseList.add(temp);
        }
    }
}
