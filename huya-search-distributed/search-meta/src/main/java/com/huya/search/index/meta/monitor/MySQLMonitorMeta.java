package com.huya.search.index.meta.monitor;

import com.huya.search.index.meta.MetaDefine;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.mysql.ColumnInfo;
import com.huya.search.index.meta.monitor.mysql.MetaDao;
import com.huya.search.index.meta.monitor.mysql.mapper.MetaMapperImpl;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.storage.DaoException;
import com.huya.search.storage.RDBFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/19.
 */
public class MySQLMonitorMeta extends MonitorOriginalMeta {

    private static final MetaMapperImpl mapper = new MetaMapperImpl();

    private static SqlSession sqlSession = RDBFactory.openSession(false);

    static {
        mapper.setShareSession(sqlSession);
    }

    @Override
    public String tag() {
        return "MySQLMonitorMeta";
    }

    @Override
    protected void addSelf(TimelineMetaDefine metaDefine) {
        MetaDao metaDao = MetaDao.newInstance(metaDefine);
        try {
            mapper.insertTableMeta(metaDao);
            metaDao.getList().forEach(mapper::insertTableColumn);
            sqlSession.commit();
        } catch (DaoException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
            sqlSession = RDBFactory.openSession();
        }
    }

    @Override
    protected void updateSelf(String table, MetaDefine metaDefine) {
        try {
            mapper.insertTableColumn(ColumnInfo.newInstance(table, metaDefine));
            sqlSession.commit();
        } catch (DaoException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
            sqlSession = RDBFactory.openSession();
        }
    }

    @Override
    protected void updateSelf(TimelineMetaDefine metaDefine) {
        TimelineMetaDefine oldMetaDefine = getTimelineMetaDefine(metaDefine.getTable());
        assert oldMetaDefine != null;
        String table = oldMetaDefine.getTable();
        metaDefine.getTreeMap().values().forEach(
                m -> {
                    if (!oldMetaDefine.exists(m)) {
                        updateSelf(table, m);
                    }
                }
        );
    }

    @Override
    protected void removeSelf(String table) {
        try {
            mapper.deleteTableMeta(table);
            mapper.deleteTableColumn(table);
            sqlSession.commit();
        } catch (DaoException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
            sqlSession = RDBFactory.openSession();
        }
    }

    @Override
    public Iterator<TimelineMetaDefine> iterator() {
        return mapper.getMetaDaoList().stream()
                .map(JsonMetaUtil::createTimelineMetaDefineFromMetaDao)
                .collect(Collectors.toList()).iterator();
    }

    @Override
    public TimelineMetaDefine getTimelineMetaDefine(String table) {
        return JsonMetaUtil.createTimelineMetaDefineFromMetaDao(mapper.getMetaDao(table));
    }

    @Override
    public void unload() {
        sqlSession.close();
    }
}
