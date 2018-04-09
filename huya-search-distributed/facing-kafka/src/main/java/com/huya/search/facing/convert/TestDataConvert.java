package com.huya.search.facing.convert;

import com.huya.search.KafkaIndexFactory;
import com.huya.search.index.data.SearchDataRow;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/23.
 */
public class TestDataConvert extends AbstractDataConvert<String, String, SearchDataRow> {
    @Override
    public SearchDataRow realConvert(int id, long offset, String s, String key) {
        return KafkaIndexFactory.tran(id, offset, s);
    }
}
