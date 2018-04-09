package com.huya.search.node;

import com.huya.search.index.opeation.PullContext;
import com.huya.search.service.TaskState;

import java.util.Objects;

/**
 * 拉取任务的状态
 * Created by zhangyiqun1@yy.com on 2018/2/28.
 */
public class PullTaskState {

    public static PullTaskState newInstance(PullContext pullContext) {
        return new PullTaskState(pullContext);
    }

    public static PullTaskState newInstance(PullContext pullContext, TaskState taskState, SalverNode salverNode) {
        return new PullTaskState(pullContext, taskState, salverNode);
    }

    private final SalverNode salverNode;

    private final PullContext pullContext;

    private final TaskState taskState;

    private PullTaskState(PullContext pullContext) {
        this(pullContext, TaskState.NOT_RUNNING, null);
    }

    private PullTaskState(PullContext pullContext, TaskState taskState, SalverNode salverNode) {
        this.pullContext = pullContext;
        this.taskState = taskState;
        this.salverNode = salverNode;
    }

    public PullContext getPullContext() {
        return pullContext;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public SalverNode getSalverNode() {
        return salverNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PullTaskState that = (PullTaskState) o;
        return Objects.equals(pullContext, that.pullContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pullContext);
    }
}
