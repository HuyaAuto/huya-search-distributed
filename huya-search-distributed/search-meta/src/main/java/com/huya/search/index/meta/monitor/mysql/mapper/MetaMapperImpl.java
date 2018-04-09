package com.huya.search.index.meta.monitor.mysql.mapper;

import com.huya.search.index.meta.monitor.mysql.ColumnInfo;
import com.huya.search.index.meta.monitor.mysql.MetaDao;
import com.huya.search.storage.AbstractRDBDao;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/23.
 */
public class MetaMapperImpl extends AbstractRDBDao<MetaDao> implements MetaMapper {

    @Override
    public List<MetaDao> getMetaDaoList() {
        return template.execute(ss -> ss.getMapper(MetaMapper.class).getMetaDaoList());
    }

    @Override
    public MetaDao getMetaDao(String table) {
        return template.execute(ss -> ss.getMapper(MetaMapper.class).getMetaDao(table));
    }

    public boolean insertTableMeta(MetaDao metaDao) {
        return template.execute(ss -> ss.getMapper(MetaMapper.class).insertTableMeta(metaDao));
    }

    @Override
    public boolean insertTableColumn(ColumnInfo columnInfo) {
        return template.execute(ss -> ss.getMapper(MetaMapper.class).insertTableColumn(columnInfo));
    }

    @Override
    public boolean deleteTableMeta(String table) {
        return template.execute(ss -> ss.getMapper(MetaMapper.class).deleteTableMeta(table));
    }

    @Override
    public boolean deleteTableColumn(String table) {
        return template.execute(ss -> ss.getMapper(MetaMapper.class).deleteTableColumn(table));
    }
}
