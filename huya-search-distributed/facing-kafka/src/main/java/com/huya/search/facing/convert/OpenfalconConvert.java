package com.huya.search.facing.convert;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.data.SearchDataRow;
import com.huya.search.util.JsonUtil;

import java.io.IOException;


/**
 * Created by zhangyiqun1@yy.com on 2018/2/27.
 */
public class OpenfalconConvert extends AbstractDataConvert<String, String, SearchDataRow> {

    @Override
    protected SearchDataRow realConvert(int id, long offset, String line, String lineKey) throws IgnorableConvertException {
        try {
            ObjectNode objectNode = (ObjectNode) JsonUtil.getObjectMapper().readTree(line);

            return SearchDataRow.buildFromJson(id, offset, objectNode);
        } catch (IOException e) {
            throw new IgnorableConvertException("io error", e);
        }
    }
}
