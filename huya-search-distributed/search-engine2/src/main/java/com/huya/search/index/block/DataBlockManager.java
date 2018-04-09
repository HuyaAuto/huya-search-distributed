package com.huya.search.index.block;

import com.huya.search.index.block.feature.*;
import com.huya.search.index.opeation.CloseContext;
import com.huya.search.index.opeation.OpenContext;
import com.huya.search.service.AbstractOrderService;

/**
 * Created by zhangyiqun1@yy.com on 2017/8/7.
 */
public abstract class DataBlockManager extends AbstractOrderService
        implements InsertFeature, ShardQueryFeature, RefreshFeature,
        RestoreFeature, ExistFeature, OpenAndCloseFeature, CheckFeature {


}
