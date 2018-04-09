package com.huya.search.restful.mysql.mapper;

import com.huya.search.restful.mysql.LogContext;
import com.huya.search.storage.AbstractRDBDao;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/2.
 */
public class LogContextMapperImpl extends AbstractRDBDao<LogContext> implements LogContextMapper {

    @Override
    public List<LogContext> getLogContextList() {
        return template.execute(ss -> ss.getMapper(LogContextMapper.class).getLogContextList());
    }

    @Override
    public boolean insertLogContext(LogContext logContext) {
        return template.execute(ss -> ss.getMapper(LogContextMapper.class).insertLogContext(logContext));
    }
}
