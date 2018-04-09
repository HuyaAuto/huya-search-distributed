package com.huya.search.index.block.feature;

import com.fasterxml.jackson.databind.JsonNode;
import com.huya.search.index.block.ShardsOperatorException;
import com.huya.search.index.opeation.InsertContext;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/8.
 */
public interface InsertFeature {

    void insert(InsertContext insertContext) throws ShardsOperatorException;

    JsonNode insertStat();
}
