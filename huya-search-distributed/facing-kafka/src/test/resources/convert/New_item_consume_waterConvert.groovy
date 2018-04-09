package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class New_item_consume_waterConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    private static final String IP = "ip";
    private static final String FILENAME = "fileName";
    private static final String LINE_NUM = "lineNum";
    private static final String MESSAGE = "message";


    @Override
    protected SearchDataRow realConvert(int id, long offset, byte[] line, byte[] key) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(key, line);

            String ip = logSvrRecord.getIp();

            String fileName = logSvrRecord.getResource();

            String num = (int)logSvrRecord.getLineId();

            String[] valueArray = logSvrRecord.getData().split(",", 2);

            long unixTime = Long.parseLong(valueArray[0]) * 1000;

            String message = valueArray[1];

            List<SearchDataItem> items = new ArrayList<>();
            items.add(SearchDataItem.newInstance(IP, ip));
            items.add(SearchDataItem.newInstance(FILENAME, fileName));
            items.add(SearchDataItem.newInstance(LINE_NUM, num));
            items.add(SearchDataItem.newInstance(MESSAGE, message));

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(unixTime)
                    .setItems(items);
        } catch (Exception e) {
            throw new IgnorableConvertException("unknown error", e);
        }
    }
}
