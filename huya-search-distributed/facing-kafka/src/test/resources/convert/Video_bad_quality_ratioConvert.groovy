package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class Video_bad_quality_ratioConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    private static final String TIMESTAMP = "its";

    private static final String AREA = "_area";

    private static final String ISP = "_isp";

    @Override
    SearchDataRow realConvert(int id, long offset, byte[] line, byte[] lineKey) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(lineKey, line);

            List<SearchDataItem> items = new ArrayList<>();
            long unixTime = 0;
            String[] kvs = logSvrRecord.getData().split("\\|");

            for (String kv : kvs) {
                String[] kvArray = kv.split(":");
                if (kvArray.length > 1) {
                    String key = kvArray[0];
                    String value = kvArray[1];
                    if (Objects.equals(TIMESTAMP, key)) {
                        unixTime = Long.parseLong(value) * 1000;
                    } else {
                        if (Objects.equals(AREA, key) || Objects.equals(ISP, key)) {
                            try {
                                items.add(SearchDataItem.newInstance(key, URLDecoder.decode(value, "UTF-8")));
                            } catch (UnsupportedEncodingException ignored) {
                                items.add(SearchDataItem.newInstance(key, ""));
                            }
                        } else {
                            items.add(SearchDataItem.newInstance(key, value));
                        }
                    }
                } else if (kvArray.length > 0) {
                    items.add(SearchDataItem.newInstance(kvArray[0], ""));
                }
            }

            if (unixTime == 0) throw new IgnorableConvertException("unixTime is zero");

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(unixTime)
                    .setItems(items);
        } catch (Exception e) {
            throw new IgnorableConvertException("unKnown error", e);
        }
    }
}
