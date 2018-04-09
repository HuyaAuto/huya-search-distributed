package com.huya.search.restful.mysql.mapper;

import com.huya.search.restful.mysql.LogContext;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/2/2.
 */
public interface LogContextMapper {

    List<LogContext> getLogContextList();

    boolean insertLogContext(LogContext logContext);

}
