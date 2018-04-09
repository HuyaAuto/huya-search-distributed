package com.huya.search.restful.action;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.index.meta.*;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.node.sep.MasterNodeService;
import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.mysql.LogContext;
import com.huya.search.restful.mysql.mapper.LogContextMapper;
import com.huya.search.restful.mysql.mapper.LogContextMapperImpl;
import com.huya.search.restful.rest.*;
import com.huya.search.util.JsonUtil;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.List;

public class MetaAction extends AbstractRestAction {

    private static final MasterNodeService NODE_SERVICE = ModulesBuilder.getInstance().createInjector().getInstance(MasterNodeService.class);

    private static final LogContextMapper LOG_CONTEXT_MAPPER = new LogContextMapperImpl();

    @Rest
    public void create(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String metaJson = new String(chain.request().httpRequest().getContent());
            NODE_SERVICE.getMasterRpcProtocol().createMeta(metaJson);
            writeToResponseString(chain, "200");
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void remove(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String table = new String(chain.request().httpRequest().getContent());
            NODE_SERVICE.getMasterRpcProtocol().removeMeta(table);
            writeToResponseString(chain, "200");
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void update(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String metaJson = new String(chain.request().httpRequest().getContent());
            ObjectNode objectNode = JsonUtil.getObjectMapper().readValue(metaJson, new TypeReference<ObjectNode>(){});
            String table = objectNode.get(MetaEnum.TABLE).textValue();
            String content = objectNode.get(MetaEnum.CONTENT).textValue();
            NODE_SERVICE.getMasterRpcProtocol().updateMeta(table, content);
            writeToResponseString(chain, "200");
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void open(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String table = new String(chain.request().httpRequest().getContent());
            NODE_SERVICE.getMasterRpcProtocol().openMeta(table);
            writeToResponseString(chain, "200");
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void close(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String table = new String(chain.request().httpRequest().getContent());
            NODE_SERVICE.getMasterRpcProtocol().closeMeta(table);
            writeToResponseString(chain, "200");
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void lastMetas(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String json = NODE_SERVICE.getMasterRpcProtocol().lastMetas();
            writeToResponseString(chain, json);
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void logContext(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            List<LogContext> logContextList = LOG_CONTEXT_MAPPER.getLogContextList();
            String json = JsonUtil.getObjectMapper().writeValueAsString(logContextList);
            writeToResponseString(chain, json);
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public String getPath() {
        return "/meta";
    }
}
