package convert

import com.huya.search.KafkaIndexFactory
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.index.data.SearchDataRow

class TestConvert extends AbstractDataConvert<byte[], byte[], SearchDataRow> {
    @Override
    SearchDataRow realConvert(int id, long offset, byte[] s, byte[] key) {
        return KafkaIndexFactory.tran(id, offset, new String(s));
    }
}