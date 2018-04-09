package com.huya.search.storage;

import com.google.common.io.Closeables;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class RDBFactory {

    private final static String RESOURCE = "mybatis.xml";

    private static Logger LOG = LoggerFactory.getLogger(RDBFactory.class);

    private static SqlSessionFactory sqlSessionFactory;

    static {
        InputStream inputStream = null;
        try {
            inputStream = RDBFactory.class.getClassLoader().getResourceAsStream(RESOURCE);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch(Exception e){
            LOG.error("init sql session factory exception", e);
        } finally{
            Closeables.closeQuietly(inputStream);
        }
    }

    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    public static SqlSession openSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }

    public static void closeSession(SqlSession session) {
        if (null != session) {
            session.close();
        }
    }
}
