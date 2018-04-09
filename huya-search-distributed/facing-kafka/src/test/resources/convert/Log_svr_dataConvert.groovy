package convert

import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.IndexSettings
import com.huya.search.constant.LogSvrConstants
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataItem
import com.huya.search.index.data.SearchDataRow
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicLong

/**
 * 日志服务检索
 *
 * @author ZhangXueJun
 * @date 2018年03月26日
 */
class Log_svr_dataConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    /**
     * 文件标识符
     */
    private static final __INODE__ = "__inode__";

    /**
     * 行号
     */
    private static final String __LINE_NUM__ = "__line_num__";

    /**
     * 目录：由fliename解析
     */
    private static final String DIR = "dir";

    /**
     * 日志名称：由fliename解析
     */
    private static final String FILENAME = "filename";

    /**
     * 日志内容
     */
    private static final String CONTENT = "content";

    /**
     * 采集ip
     */
    private static final String IP = "ip";

    @Override
    protected SearchDataRow realConvert(int id, long offset, byte[] line, byte[] key) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(key, line);
            List<SearchDataItem> items = new ArrayList<>();

            // 写入业务标识
            items.add(SearchDataItem.newInstance(LogSvrConstants.__APP__, logSvrRecord.getApp()));
            items.add(SearchDataItem.newInstance(LogSvrConstants.__MODULE__, logSvrRecord.getModule()));
            items.add(SearchDataItem.newInstance(LogSvrConstants.__TYPE__, logSvrRecord.getType()));

            // 写入文件信息
            items.add(SearchDataItem.newInstance(__INODE__, StringUtils.isEmpty(logSvrRecord.getInode()) ? -1 : Long.valueOf(logSvrRecord.getInode())));
            items.add(SearchDataItem.newInstance(__LINE_NUM__, logSvrRecord.getLineId()));

            String fileName = logSvrRecord.getResource();
            int index = fileName.lastIndexOf("/");
            items.add(SearchDataItem.newInstance(IP, logSvrRecord.getIp()));
            items.add(SearchDataItem.newInstance(DIR, fileName.substring(0, index)));
            items.add(SearchDataItem.newInstance(FILENAME, fileName.substring(index + 1, fileName.length())));

            // 写入文件内容
            items.add(SearchDataItem.newInstance(CONTENT,
                    logSvrRecord.getData(), IndexSettings.getDynamicAnalyzer(logSvrRecord.getApp(), logSvrRecord.getModule(), logSvrRecord.getType())));
            long unixTime = logSvrRecord.getTimestamp();
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