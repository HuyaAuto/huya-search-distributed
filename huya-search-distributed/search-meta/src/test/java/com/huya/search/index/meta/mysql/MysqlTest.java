package com.huya.search.index.meta.mysql;

import com.google.common.collect.Lists;
import com.huya.search.index.meta.TimelineMetaDefine;
import com.huya.search.index.meta.monitor.mysql.ColumnInfo;
import com.huya.search.index.meta.monitor.mysql.MetaDao;
import com.huya.search.index.meta.monitor.mysql.mapper.MetaMapperImpl;
import com.huya.search.index.meta.util.JsonMetaUtil;
import com.huya.search.storage.DaoException;
import com.huya.search.storage.RDBFactory;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/23.
 */
public class MysqlTest {

    @Test
    public void selectTest() {
        MetaMapperImpl metaMapper = new MetaMapperImpl();
        List<MetaDao> list = metaMapper.getMetaDaoList();
        Assert.assertTrue(list.size() > 0);
        MetaDao metaDao = metaMapper.getMetaDao("test");
        Assert.assertTrue(metaDao != null);
    }

    @Test
    public void insertTest() {
        MetaMapperImpl metaMapper = new MetaMapperImpl();
        MetaDao metaDao = new MetaDao();
        metaDao.setTable("insertTest")
                .setAccuracy(0.00000001);
        ColumnInfo columnInfo1 = new ColumnInfo();
        columnInfo1.setTable("insertTest")
                .setPartitionName("2017-10-20_1_10")
                .setColumnInfo("{}");
        ColumnInfo columnInfo2 = new ColumnInfo();
        columnInfo2.setTable("insertTest")
                .setPartitionName("2017-10-20_1_11")
                .setColumnInfo("{}");

        List<ColumnInfo> list = Lists.newArrayList(columnInfo1, columnInfo2);
        metaDao.setList(list);
        SqlSession sqlSession = RDBFactory.openSession(false);
        try {
            metaMapper.setShareSession(sqlSession);
            metaMapper.insertTableMeta(metaDao);
            metaDao.getList().forEach(metaMapper::insertTableColumn);
            sqlSession.commit();
        } catch (DaoException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void metaDefineEqualTest() {
        MetaMapperImpl metaMapper = new MetaMapperImpl();
        MetaDao metaDao = metaMapper.getMetaDao("gamelive_huya_hysignal_tx_response_time_log");
        TimelineMetaDefine metaDefine1 = JsonMetaUtil.createTimelineMetaDefineFromMetaDao(metaDao);
        TimelineMetaDefine metaDefine2 = JsonMetaUtil.createTimelineMetaDefineFromMetaDao(metaDao);
        metaDefine1.getTreeMap().values().forEach(
                m -> {
                    System.out.println(metaDefine2.exists(m));
                }
        );
    }

    @Test
    public void deleteMeta() {
        MetaMapperImpl metaMapper = new MetaMapperImpl();
        SqlSession sqlSession = RDBFactory.openSession(false);
        try {
            metaMapper.setShareSession(sqlSession);
            metaMapper.deleteTableMeta("test");
            metaMapper.deleteTableColumn("test");
            sqlSession.commit();
        } catch (DaoException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }
}
