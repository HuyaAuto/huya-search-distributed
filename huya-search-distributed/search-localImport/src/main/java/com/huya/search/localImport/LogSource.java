package com.huya.search.localImport;

import com.huya.search.index.data.SearchDataRow;
import com.huya.search.index.meta.CorrespondTable;

import java.util.Iterator;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/16.
 */
public interface LogSource extends Iterator<SearchDataRow>, CorrespondTable {


}
