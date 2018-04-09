package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class Web_nginxConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    private static final String IP = "ip";

    private static final String HOST = "host";

    private static final String REMOTE_ADDR = "remote_addr";

    private static final String UPSTREAM_ADDR = "upstream_addr";

    private static final String REQUEST = "request";

    private static final String STATUS = "status";

    private static final String BODY_BYTES_SENT = "body_bytes_sent";

    private static final String HTTP_REFERER = "http_referer";

    private static final String HTTP_USER_AGENT = "http_user_agent";

    private static final String REQUEST_TIME = "request_time";

    private static final String UPSTREAM_RESPONSE_TIME = "upstream_response_time";

    private static final String SPLIT = "#_#";

    private static final String BAR = "-";

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z").withLocale(Locale.ENGLISH);

    @Override
    protected SearchDataRow realConvert(int id, long offset, byte[] line, byte[] lineKey) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(lineKey, line);

            String[] lineArray = logSvrRecord.getData().split(SPLIT);

            if (lineArray.length != 11) throw new IgnorableConvertException("line array is not 11");

            String timestamp = lineArray[3].substring(1, lineArray[3].length() - 1);

            long unixTime = FORMATTER.parseDateTime(timestamp).getMillis();

            List<SearchDataItem> items = new ArrayList<>();

            items.add(SearchDataItem.newInstance(IP, logSvrRecord.getIp()));

            items.add(SearchDataItem.newInstance(HOST, lineArray[0]));
            items.add(SearchDataItem.newInstance(REMOTE_ADDR, lineArray[1]));
            items.add(SearchDataItem.newInstance(UPSTREAM_ADDR, lineArray[2]));
            items.add(SearchDataItem.newInstance(REQUEST, lineArray[4]));
            items.add(SearchDataItem.newInstance(STATUS, lineArray[5]));
            items.add(SearchDataItem.newInstance(BODY_BYTES_SENT, lineArray[6]));

            String httpReferer = lineArray[7];

            if (!Objects.equals(httpReferer, BAR)) {
                items.add(SearchDataItem.newInstance(HTTP_REFERER, lineArray[7]));
            }

            items.add(SearchDataItem.newInstance(HTTP_USER_AGENT, lineArray[8]));
            items.add(SearchDataItem.newInstance(REQUEST_TIME, lineArray[9]));

            String upstreamResponseTime = lineArray[10];

            if (!Objects.equals(upstreamResponseTime, BAR)) {
                items.add(SearchDataItem.newInstance(UPSTREAM_RESPONSE_TIME, lineArray[10]));
            }

            return new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(unixTime)
                    .setItems(items);
        } catch (Exception e) {
            throw new IgnorableConvertException(e);
        }
    }
}
