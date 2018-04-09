package com.huya.search.restful.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huya.search.inject.ModulesBuilder;
import com.huya.search.node.Cluster;
import com.huya.search.node.NodeBaseEntry;
import com.huya.search.node.SalverNode;
import com.huya.search.node.sep.MasterNodeService;
import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.rest.*;
import com.huya.search.util.JsonUtil;
import org.apache.avro.AvroRemoteException;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Set;

/**
 * Created by zhangyiqun1@yy.com on 2017/11/18.
 */
public class SalversAction extends AbstractRestAction {

    private static final MasterNodeService NODE_SERVICE = ModulesBuilder.getInstance().createInjector().getInstance(MasterNodeService.class);

    private static final Cluster CLUSTER = ModulesBuilder.getInstance().createInjector().getInstance(Cluster.class);

    @Rest
    public void list(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        Set<SalverNode> salvers = CLUSTER.getAllSalvers();
        ArrayNode arrayNode = JsonUtil.createArray();
        salvers.forEach(salverNode -> {
            ObjectNode salverObjectNode = JsonUtil.createObject();
            NodeBaseEntry nodeBaseEntry = salverNode.getNodeEntry();
            salverObjectNode.put("ip", nodeBaseEntry.getServiceHost());
            salverObjectNode.put("port", nodeBaseEntry.getServicePort());

            //todo 补全逻辑
//            int v = salverNode.getVirtualNodeNum();
//            for (int i = 0; i < v; i++) {
//                ObjectNode objectNode = JsonUtil.createObject();
//                Set<PullContext> pullContextSet = salverNode.getAssignObject(i);
//                ArrayNode pullArrayNode = JsonUtil.getObjectMapper().valueToTree(pullContextSet);
//                objectNode.set("pull", pullArrayNode);
//                salverObjectNode.set("virtualNode"+ i, objectNode);
//            }
            arrayNode.add(salverObjectNode);

        });
        try {
            writeToResponseString(chain, JsonUtil.getPrettyObjectMapper().writeValueAsString(arrayNode));
        } catch (JsonProcessingException e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }


    @Rest
    public void shutdown(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            NODE_SERVICE.getMasterRpcProtocol().shutdown();
            writeToResponseString(chain, "200");
        } catch (AvroRemoteException e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public String getPath() {
        return "/salvers";
    }
}
