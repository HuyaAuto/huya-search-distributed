package com.huya.search.tasks;

import com.huya.search.hash.AssignObject;
import com.huya.search.index.opeation.PullContext;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.rpc.RpcService;
import com.huya.search.service.TaskAttr;
import com.huya.search.service.TaskException;
import com.huya.search.service.AbstractTask;
import org.apache.avro.AvroRemoteException;

import java.util.Objects;

/**
 * Created by zhangyiqun1@yy.com on 2018/1/3.
 */
public class PullTask extends AbstractTask<PullTask.PullTaskAttr> implements AssignObject {

    public static PullTask newInstance(PullContext pullContext) {
        return new PullTask(pullContext);
    }

    private PullContext pullContext;

    private RpcService rpcService = ModulesBuilder.getInstance().createInjector().getInstance(RpcService.class);

    private PullTask(PullContext pullContext) {
        this.pullContext = pullContext;
    }

    @Override
    protected void doStart(PullTask.PullTaskAttr pullTaskAttr) throws TaskException {
        try {
            rpcService.openPullTask(pullTaskAttr.nodeBaseEntry, pullContext);
        } catch (AvroRemoteException e) {
            throw new TaskException(e);
        }
    }

    @Override
    protected void doStop(PullTask.PullTaskAttr pullTaskAttr) throws TaskException {
        try {
            rpcService.closePullTask(pullTaskAttr.nodeBaseEntry, pullContext);
        } catch (AvroRemoteException e) {
            throw new TaskException(e);
        }
    }

    @Override
    protected void doStartException() {
        //do nothing
    }

    @Override
    protected void doStopException() {
        //do nothing
    }

    @Override
    protected void doRunException() {
        //do nothing
    }

    @Override
    protected void doClearException() {
        //do nothing
    }

    public PullContext getPullContext() {
        return pullContext;
    }

    @Override
    public int hash() {
        return pullContext.hash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PullTask pullTask = (PullTask) o;
        return Objects.equals(pullContext, pullTask.pullContext);
    }

    @Override
    public int hashCode() {
        return pullContext.hashCode();
    }

    @Override
    public String toString() {
        return "PullTask{" +
                "pullContext=" + pullContext +
                '}';
    }

    public static class PullTaskAttr implements TaskAttr {

        public static PullTaskAttr newInstance(NodeBaseEntry nodeBaseEntry) {
            return new PullTaskAttr(nodeBaseEntry);
        }

        private NodeBaseEntry nodeBaseEntry;

        private PullTaskAttr(NodeBaseEntry nodeBaseEntry) {
            this.nodeBaseEntry = nodeBaseEntry;
        }

    }
}
