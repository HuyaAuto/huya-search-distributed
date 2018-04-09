package convert

import com.duowan.datawarehouse.exception.ActionLogParserException
import com.duowan.datawarehouse.model.ActionLog
import com.google.protobuf.InvalidProtocolBufferException
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow
import com.huya.search.util.JodaUtil

/**
 * huya_action_log转化类
 *
 * @author ZhangXueJun
 * @date 2018年03月19日
 */
class Huya_action_logConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    @Override
    SearchDataRow realConvert(int id, long offset, byte[] line, byte[] lineKey) throws IgnorableConvertException {
        try {
            List<SearchDataItem> items = new ArrayList<>();
            String[] arr = new String(line, "UTF-8").split(String.valueOf(ActionLog.fieldsTerminatedChar));
            int i = 0;
//            items.add(SearchDataItem.newInstance("product", arr[i++]))
            items.add(SearchDataItem.newInstance("action", arr[i++]))
            items.add(SearchDataItem.newInstance("duration", arr[i++]))
            items.add(SearchDataItem.newInstance("atime", arr[i++]))
            items.add(SearchDataItem.newInstance("flag", arr[i++]))
            items.add(SearchDataItem.newInstance("mid", arr[i++]))
            items.add(SearchDataItem.newInstance("ip", arr[i++]))
            items.add(SearchDataItem.newInstance("proxyIp", arr[i++]))
            String stime = arr[i++];
            long unixTime = JodaUtil.getUnixTime(stime)
            items.add(SearchDataItem.newInstance("stime", stime))
            items.add(SearchDataItem.newInstance("passport", arr[i++]))
            items.add(SearchDataItem.newInstance("game", arr[i++]))
            items.add(SearchDataItem.newInstance("gameServer", arr[i++]))
            items.add(SearchDataItem.newInstance("runSource", arr[i++]))
            items.add(SearchDataItem.newInstance("iver", arr[i++]))
            items.add(SearchDataItem.newInstance("uver", arr[i++]))
            items.add(SearchDataItem.newInstance("channel", arr[i++]))
            items.add(SearchDataItem.newInstance("referrer", arr[i++]))
            items.add(SearchDataItem.newInstance("pid", arr[i++]))
            items.add(SearchDataItem.newInstance("os",  arr[i++]))
            items.add(SearchDataItem.newInstance("browerVersion", arr[i++]))
            items.add(SearchDataItem.newInstance("screenResolution", arr[i++]))
            items.add(SearchDataItem.newInstance("screenColor", arr[i++]))
            items.add(SearchDataItem.newInstance("localLanguage", arr[i++]))
            items.add(SearchDataItem.newInstance("java", arr[i++]))
            items.add(SearchDataItem.newInstance("cookies", arr[i++]))
            items.add(SearchDataItem.newInstance("netVersion", arr[i++]))
            items.add(SearchDataItem.newInstance("country", arr[i++]))
            items.add(SearchDataItem.newInstance("province", arr[i++]))
            items.add(SearchDataItem.newInstance("city", arr[i++]))
            items.add(SearchDataItem.newInstance("isp", arr[i++]))
            items.add(SearchDataItem.newInstance("level1", arr[i++]))
            items.add(SearchDataItem.newInstance("level2", arr[i++]))
            items.add(SearchDataItem.newInstance("level3", arr[i++]))
            items.add(SearchDataItem.newInstance("level4", arr[i++]))
            items.add(SearchDataItem.newInstance("level5", arr[i++]))
//            items.add(SearchDataItem.newInstance("levels", arr[i++]))
//            items.add(SearchDataItem.newInstance("ext", arr.length >= i ? arr[i++]: "{}"))

            SearchDataRow searchDataRow = new SearchDataRow()
                    .setId(id)
                    .setOffset(offset)
                    .setUnixTime(unixTime)
                    .setItems(items);
            return searchDataRow;
        } catch (Exception e) {
            if (Exception instanceof ActionLogParserException || Exception instanceof InvalidProtocolBufferException.InvalidWireTypeException) {
                // 忽略
            }
            throw new IgnorableConvertException("unKnown error", e);
        }
    }

}
