package com.huya.search.index.block.feature;

import com.huya.search.index.opeation.DisRefreshContext;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/26.
 */
public interface DisRefreshFeature {

    void refresh(DisRefreshContext disRefreshContext);

}
