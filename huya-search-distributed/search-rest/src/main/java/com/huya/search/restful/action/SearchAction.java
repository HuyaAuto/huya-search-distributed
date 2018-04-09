package com.huya.search.restful.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.data.QueryResult;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.node.sep.MasterNodeService;
import com.huya.search.partition.PartitionCycle;
import com.huya.search.partition.PartitionGrain;
import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.mysql.QueryDetail;
import com.huya.search.restful.mysql.mapper.QueryDetailMapper;
import com.huya.search.restful.mysql.mapper.QueryDetailMapperImpl;
import com.huya.search.restful.rest.*;
import com.huya.search.rpc.QueryDetailResult;
import com.huya.search.util.JodaUtil;
import com.huya.search.util.JsonUtil;
import com.huya.search.util.ThreadPoolSingleton;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SearchAction extends AbstractRestAction {

    private static final MasterNodeService NODE_SERVICE = ModulesBuilder.getInstance().createInjector().getInstance(MasterNodeService.class);

    private static final QueryDetailMapper QUERY_DETAIL_MAPPER = new QueryDetailMapperImpl();

    private Logger LOG = LoggerFactory.getLogger(SearchAction.class);

    @Rest
    public void sql(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        String sql = new String(chain.request().httpRequest().getContent());
        LOG.info("start to query :\n" + sql);
        try {
            QueryDetailResult rpcResult = NODE_SERVICE.getMasterRpcProtocol().sql(sql);
            QueryResult queryResult = rpcResult.getQueryResult();
            writeToResponseString(chain, queryResult.toString());
            ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> {
                QueryDetail queryDetail = new QueryDetail()
                        .setSql(sql)
                        .setQueryTime(JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER.print(System.currentTimeMillis()))
                        .setUseTime(queryResult.getRunTime())
                        .setResultLen(queryResult.size())
                        .setTable(rpcResult.getTable());
                try {
                    QUERY_DETAIL_MAPPER.insertQueryDetail(queryDetail);
                } catch (Exception e) {
                    LOG.error("insert query detail error", e);
                }
            });
        } catch (Exception e) {
            LOG.error("netty query error", e);
            String exceptionStackTrace = ExceptionUtils.getStackTrace(e);
            reportError(chain, e);
            ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> {
                QueryDetail queryDetail = new QueryDetail()
                        .setSql(sql)
                        .setQueryTime(JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER.print(System.currentTimeMillis()))
                        .setExceptionMsg(exceptionStackTrace);
                try {
                    QUERY_DETAIL_MAPPER.insertQueryDetail(queryDetail);
                } catch (Exception queryE) {
                    LOG.error("insert query detail error", queryE);
                }
            });
        }
    }

    @Rest
    public void count(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        String[] temp = new String(chain.request().httpRequest().getContent()).split("#");
        String table = temp[0];
        String where = temp[1].trim();
        String sql   = "select count(*) from " + table + " where ";
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        List<Future> futureList = new ArrayList<>();
        timeRangeConditionByPartition().forEach(partitionCycle -> {
            futureList.add(ThreadPoolSingleton.getInstance().getExecutorService(ThreadPoolSingleton.Names.GENERIC).submit(() -> {
                String low = new DateTime(partitionCycle.getFloor()).toString(JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER);
                String up  = new DateTime(partitionCycle.getCeil()).toString(JodaUtil.YYYY_MM_DD_HH_MM_SS_FORMATTER);
                String condition = null;
                if (where.length() > 0) {
                    condition = " pr >= '" + low + "' and pr < '" + up + "' and ( " + where + " )";
                }
                else {
                    condition = " pr >= '" + low + "' and pr < '" + up + "'";
                }
                String withConditionSql = sql + condition;
                System.out.println("sql : " + withConditionSql);
                QueryDetailResult rpcResult = NODE_SERVICE.getMasterRpcProtocol().sql(withConditionSql);
                QueryResult queryResult = rpcResult.getQueryResult();
                objectNode.set(low, queryResult.toObject());
            }));
        });
        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        writeToResponseString(chain, objectNode.toString());
    }

    private List<PartitionCycle> timeRangeConditionByPartition() {
        List<PartitionCycle> timeRangeList = new ArrayList<>();
        PartitionCycle partitionCycle = new PartitionCycle(new DateTime(), PartitionGrain.HOUR);
        while (timeRangeList.size() < 6) {

            timeRangeList.add(partitionCycle);
            partitionCycle = partitionCycle.prev();
        }
        return timeRangeList;
    }

    @Rest
    public void queryList(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        String timeLow = chain.request().httpRequest().getParameterFirst("timeLow");
        String timeUp  = chain.request().httpRequest().getParameterFirst("timeUp");

        List<QueryDetail> queryDetails = QUERY_DETAIL_MAPPER.getQueryDetailList(timeLow, timeUp);
        try {
            String queryJson = JsonUtil.getObjectMapper().writeValueAsString(queryDetails);
            writeToResponseString(chain, queryJson);
        } catch (JsonProcessingException e) {
            LOG.error("get query list error", e);
            String exceptionStackTrace = ExceptionUtils.getStackTrace(e);
            writeToResponseString(chain, exceptionStackTrace);
        }
    }



    @Override
    public String getPath() {
        return "/search";
    }


}
