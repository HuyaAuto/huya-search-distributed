package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class Hiido_live_server_waterConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    private static final String IP = "ip";
    private static final String EVENT = "event";
    private static final String MESSAGE = "message";

    @Override
    SearchDataRow realConvert(int id, long offset, byte[] line, byte[] key) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(key, line);

            String ip = logSvrRecord.getIp();

            String[] temp = logSvrRecord.getData().split("\u0005", 3);

            if (temp.length < 3) throw new IgnorableConvertException("line date enough 3");

            String timestamp = temp[0];
            String event = temp[1];
            String message = temp[2];

            List<SearchDataItem> items = new ArrayList<>();
            items.add(SearchDataItem.newInstance(IP, ip));
            items.add(SearchDataItem.newInstance(EVENT, event));
            items.add(SearchDataItem.newInstance(MESSAGE, message));

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(timestamp)
                    .setItems(items);
        } catch (Exception e) {
            throw new IgnorableConvertException("unKnow error", e);
        }
    }

}

