package com.huya.search.storage;

import org.apache.ibatis.session.SqlSession;

public abstract class AbstractRDBDao<T> {

	protected RDBDaoTemplate template = new RDBDaoTemplate();

	public void setShareSession(SqlSession session) {
		template.setShareSession(session);
	}

}
