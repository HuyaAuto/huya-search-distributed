package com.huya.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huya.search.index.data.SearchDataItem;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.util.JsonUtil;
import com.yy.dc.avro.SimpleEvent;
import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class KafkaIndexFactory {

    public static <T> SearchDataRow tran(int id, long offset, T t) {
        if (t != null) {
            if (t instanceof SimpleEvent) {
                SimpleEvent se = (SimpleEvent) t;
                String msg = new String(se.getBody(), StandardCharsets.UTF_8);
                Map<String, String> map = se.getHeaders();
                map.put("msg", msg);
                return tranDataRow(id, offset, tranList(map));
            }
            else if (t instanceof String) {
                String str = (String) t;
                if (!Objects.equals(str, "")) {
                    try {
                        Map<String, Object> map = JsonUtil.getObjectMapper().readValue(str, new TypeReference<Map<String, Object>>(){});
                        map.remove("timestamp");
                        return tranDataRow(id, offset, tranList(map));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private static List<SearchDataItem> tranList(Map<String, ?> map) {
        List<SearchDataItem> items = new ArrayList<>();
        map.forEach((key, value) -> {
            items.add(SearchDataItem.newInstance(key, value));
        });
        return items;
    }

    private static final Random random = new Random();

    private static SearchDataRow tranDataRow(int id, long offset, List<SearchDataItem> items) {
//        long timestamp = random.nextInt() % 2 == 0 ? DateTime.now().getMillis() - 60*60*1000 : DateTime.now().getMillis();
        long timestamp = DateTime.now().getMillis();
        return new SearchDataRow()
                .setId(id)
                .setOffset(offset)
                .setUnixTime(timestamp)
                .setItems(items);
    }

    private static SearchDataRow tranDataRow(int id, long offset, String timestamp, List<SearchDataItem> items) {
        return new SearchDataRow()
                .setId(id)
                .setOffset(offset)
                .setUnixTime(timestamp)
                .setItems(items);
    }

}
