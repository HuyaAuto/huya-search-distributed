import com.huya.search.KafkaIndexFactory
import com.huya.search.facing.convert.AbstractDataConvert
import com.huya.search.index.data.SearchDataRow

class TestConvert extends AbstractDataConvert<String, String, SearchDataRow> {
    @Override
    SearchDataRow realConvert(int id, long offset, String s, String key) {
        return KafkaIndexFactory.tran(id, offset, s);
    }
}