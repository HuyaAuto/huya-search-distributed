package com.huya.search.index.meta.monitor.mysql.mapper;

import com.huya.search.index.meta.monitor.mysql.ColumnInfo;
import com.huya.search.index.meta.monitor.mysql.MetaDao;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/23.
 */
public interface MetaMapper {

    List<MetaDao> getMetaDaoList();

    MetaDao getMetaDao(String table);

    boolean insertTableMeta(MetaDao metaDao);

    boolean insertTableColumn(ColumnInfo columnInfo);

    boolean deleteTableMeta(String table);

    boolean deleteTableColumn(String table);
}
