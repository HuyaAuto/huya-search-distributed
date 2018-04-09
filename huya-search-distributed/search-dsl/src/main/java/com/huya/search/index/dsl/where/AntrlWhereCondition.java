package com.huya.search.index.dsl.where;

import com.huya.search.index.meta.TimelineMetaDefine;


/**
 * Created by zhangyiqun1@yy.com on 2017/9/11.
 */
public class AntrlWhereCondition extends WhereCondition {

    public static WhereCondition newInstance(TimelineMetaDefine timelineMetaDefine, WhereStatement whereStatement) {
        return new AntrlWhereCondition(timelineMetaDefine, whereStatement);
    }

    private AntrlWhereCondition(TimelineMetaDefine timelineMetaDefine, WhereStatement whereStatement) {
        super(timelineMetaDefine, whereStatement);
    }


}
