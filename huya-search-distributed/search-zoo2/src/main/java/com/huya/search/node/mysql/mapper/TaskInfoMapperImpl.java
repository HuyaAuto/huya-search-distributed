package com.huya.search.node.mysql.mapper;

import com.huya.search.node.mysql.TaskInfo;
import com.huya.search.storage.AbstractRDBDao;

import java.util.List;

/**
 * Created by zhangyiqun1@yy.com on 2018/3/1.
 */
public class TaskInfoMapperImpl extends AbstractRDBDao<TaskInfo> implements TaskInfoMapper {

    @Override
    public boolean insertTaskInfo(TaskInfo taskInfo) {
        return template.execute(ss -> ss.getMapper(TaskInfoMapper.class).insertTaskInfo(taskInfo));
    }

    @Override
    public TaskInfo getTaskInfo(String table, int shardId) {
        return template.execute(ss -> ss.getMapper(TaskInfoMapper.class).getTaskInfo(table, shardId));
    }

    @Override
    public List<TaskInfo> getAllTaskInfo() {
        return template.execute(ss -> ss.getMapper(TaskInfoMapper.class).getAllTaskInfo());
    }
}
