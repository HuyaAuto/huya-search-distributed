package com.huya.search.index.block.feature;

import com.huya.search.index.opeation.RefreshContext;

public interface RefreshFeature {

    void refresh(RefreshContext refreshContext);
}
