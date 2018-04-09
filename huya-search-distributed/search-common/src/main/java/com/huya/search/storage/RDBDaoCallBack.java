package com.huya.search.storage;

import org.apache.ibatis.session.SqlSession;


public interface RDBDaoCallBack<T> {

    public T execute(SqlSession ss) throws DaoException;
}
