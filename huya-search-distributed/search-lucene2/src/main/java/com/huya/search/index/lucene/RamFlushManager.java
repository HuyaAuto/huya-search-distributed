package com.huya.search.index.lucene;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.huya.search.SearchException;
import com.huya.search.service.AbstractLifecycleService;
import com.huya.search.util.JsonUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiqun1@yy.com on 2017/12/5.
 */
@Singleton
public class RamFlushManager extends AbstractLifecycleService {

    private static final long INTERVAL = 5000L; //毫秒

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    private Vector<RamTwoPhaseLuceneOperator> flushVector = new Vector<>();

    private Future<?> flushFuture;

    private FlushTask flushTask = new FlushTask();

    @Inject
    public RamFlushManager() {}

    @Override
    protected void doStart() throws SearchException {
        flushFuture = scheduledExecutorService.scheduleAtFixedRate(flushTask, INTERVAL, INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void doStop() throws SearchException {
        flushFuture.cancel(true);
    }

    @Override
    protected void doClose() throws SearchException {
        scheduledExecutorService.shutdown();
        flushVector.clear();
    }

    public void registerFlusher(RamTwoPhaseLuceneOperator luceneOperator) {
        flushVector.add(luceneOperator);
    }

    public void unRegisterFlusher(RamTwoPhaseLuceneOperator luceneOperator) {
        flushVector.remove(luceneOperator);
    }

    public String getRegisterFlusher() throws JsonProcessingException {
        ArrayNode arrayNode = JsonUtil.getObjectMapper().createArrayNode();
        flushVector.forEach(ramTwoPhaseLuceneOperator -> arrayNode.add(ramTwoPhaseLuceneOperator.tag()));
        return JsonUtil.getPrettyObjectMapper().writeValueAsString(arrayNode);
    }


    @Override
    public String getName() {
        return "ramFlushManager";
    }

    class FlushTask extends TimerTask {

        @Override
        public void run() {
            for (RamTwoPhaseLuceneOperator luceneOperator : flushVector) {
                try {
                    if (luceneOperator.getRamPublicUseFreq().writeIdleTime() < INTERVAL) {
                        luceneOperator.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
