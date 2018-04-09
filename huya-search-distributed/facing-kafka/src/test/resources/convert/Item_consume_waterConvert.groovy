package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class Item_consume_waterConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    private static final String IP = "ip";

    private static final String ITEM_CONSUME_WATER = "item_consume_water";

    @Override
    SearchDataRow realConvert(int id, long offset, byte[] line, byte[] key) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(key, line);

            String[] temp = logSvrRecord.getData().split("\\|");
            if (temp.length == 2) {
                List<SearchDataItem> items = new ArrayList<>();
                items.add(SearchDataItem.newInstance(IP, temp[0]));
                String[] strItems = temp[1].split(",");
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
            else {
                throw new IgnorableConvertException("unParse log");
            }
        } catch (Exception e) {
            throw new IgnorableConvertException("unKnown error", e);
        }
    }
}