package com.huya.search.storage;


import org.apache.ibatis.session.SqlSession;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/20.
 */
public class RDBDaoTemplate {

    private boolean isShareSession = false;

    private SqlSession session;

    public void setShareSession(SqlSession session) {
        isShareSession = true;
        this.session = session;
    }

    public <T>T execute(RDBDaoCallBack<T> callBack) throws DaoException {
        SqlSession ss = null;
        try {
            ss = isShareSession ? this.session : RDBFactory.openSession();
            T t = callBack.execute(ss);
            if (!isShareSession) {
                ss.commit();
            }
            return t;
        }catch(Exception ex){
            throw new DaoException("dao template execute exception", ex);
        }finally{
            if (!isShareSession) RDBFactory.closeSession(ss);
        }
    }



}
