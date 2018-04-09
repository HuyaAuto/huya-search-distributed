package convert

import com.fasterxml.jackson.databind.node.ObjectNode
import com.huya.beelogsvr.model.LogSvrRecord
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.facing.convert.IgnorableConvertException
import com.huya.search.index.data.SearchDataRow
import com.huya.search.util.JsonUtil

/**
 * Created by zhangyiqun1@yy.com on 2018/3/6.
 */
class Open_falconConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {

    @Override
    protected SearchDataRow realConvert(int id, long offset, byte[] line, byte[] lineKey) throws IgnorableConvertException {
        try {
            LogSvrRecord logSvrRecord = LogSvrRecord.parse(lineKey, line);

            ObjectNode objectNode = (ObjectNode) JsonUtil.getObjectMapper().readTree(logSvrRecord.getData());

            return SearchDataRow.buildFromJson(id, offset, objectNode);
        } catch (IOException e) {
            throw new IgnorableConvertException("io error", e);
        }
    }
}
