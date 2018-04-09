package com.huya.search.restful.mysql.mapper;

import com.huya.search.restful.mysql.QueryDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/20.
 */
public interface QueryDetailMapper {

    List<QueryDetail> getQueryDetailList(@Param("timeLow") String timeLow,
                                         @Param("timeUp") String timeUp);

    boolean insertQueryDetail(QueryDetail queryDetail);
}
