package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class Live_serverConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    private static final Pattern pattern = Pattern.compile("\\[(\\d+)ms]\\d*");

    private static final String SPLIT = "|";

    private static final String IP = "ip";
    private static final String DELAY = "delay";
    private static final String EVENT = "event";
    private static final String PROTOCOL = "protocol";

    @Override
    SearchDataRow realConvert(int id, long offset, byte[] lineByte, byte[] keyByte) throws IgnorableConvertException {
        try {
            String timestamp = null;

            int historyIndex = 0;
            int num = 0;

            List<SearchDataItem> items = new ArrayList<>();

            LogSvrRecord logSvrRecord = LogSvrRecord.parse(keyByte, lineByte);

            String line = logSvrRecord.getData();

            for (int index = line.indexOf(SPLIT); index >= 0; index = line.indexOf(SPLIT, index + 1)) {
                String value = line.substring(historyIndex, index);
                historyIndex = index + 1;
                num++;
                switch (num) {
                    case 1: items.add(SearchDataItem.newInstance(IP, value)); break;
                    case 2: timestamp = value; break;
                    case 3:
                        Matcher matcher = pattern.matcher(value);
                        if (matcher.find()) {
                            items.add(SearchDataItem.newInstance(DELAY, Integer.parseInt(matcher.group(1))));
                        } else {
                            items.add(SearchDataItem.newInstance(EVENT, value));
                            num++;
                        }
                        break;
                    case 4:
                        items.add(SearchDataItem.newInstance(EVENT, value));
                }

                if (num == 4) {
                    break;
                }
            }

            items.add(SearchDataItem.newInstance(PROTOCOL, line.substring(historyIndex)));

            if (timestamp == null) throw new IgnorableConvertException("timestamp is null");

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(timestamp)
                    .setItems(items);
        } catch (Exception e) {
            throw new IgnorableConvertException("unKnown error", e);
        }
    }
}
