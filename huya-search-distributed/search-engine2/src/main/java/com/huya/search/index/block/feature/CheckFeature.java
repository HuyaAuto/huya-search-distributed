package com.huya.search.index.block.feature;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/23.
 */
public interface CheckFeature {

    /**
     * 检查是否命中 （可能是空闲可移除等等）
     * @return 是否命中
     */
    boolean check();
}
