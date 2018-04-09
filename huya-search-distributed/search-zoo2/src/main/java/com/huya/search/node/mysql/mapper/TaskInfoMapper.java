package com.huya.search.node.mysql.mapper;

import com.huya.search.node.mysql.TaskInfo;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/1.
 */
public interface TaskInfoMapper {

    boolean insertTaskInfo(TaskInfo taskInfo);

    TaskInfo getTaskInfo(String table, int shardId);

    List<TaskInfo> getAllTaskInfo();
}
