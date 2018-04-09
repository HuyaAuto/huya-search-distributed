package com.huya.search.restful.mysql.mapper;

import com.huya.search.restful.mysql.QueryDetail;
import com.huya.search.storage.AbstractRDBDao;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/20.
 */
public class QueryDetailMapperImpl extends AbstractRDBDao<QueryDetail> implements QueryDetailMapper {

    @Override
    public List<QueryDetail> getQueryDetailList(String timeLow, String timeUp) {
        return template.execute(ss -> ss.getMapper(QueryDetailMapper.class).getQueryDetailList(timeLow, timeUp));
    }

    @Override
    public boolean insertQueryDetail(QueryDetail queryDetail) {
        return template.execute(ss -> ss.getMapper(QueryDetailMapper.class).insertQueryDetail(queryDetail));
    }
}
