package com.huya.search.restful.action;

import com.huya.search.inject.ModulesBuilder;
import com.huya.search.node.sep.MasterNodeService;
import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.http.HttpRequest;
import com.huya.search.restful.rest.*;
import org.apache.avro.AvroRemoteException;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.stream.Stream;

public class InsertAction extends AbstractRestAction {

//    private static final Engine ENGINE = ModulesBuilder.getInstance().createInjector().getInstance(IndexEngine.class);

    private static final MasterNodeService NODE_SERVICE = ModulesBuilder.getInstance().createInjector().getInstance(MasterNodeService.class);

//    private static final LazyFileSystem FILE_SYSTEM = ModulesBuilder.getInstance().createInjector().getInstance(LazyFileSystem.class);

//    /**
//     * 测试插入接口，只用于测试插入数据
//     * @param chain 调用链
//     * @throws ActionException action 异常
//     */
//    @Rest
//    @SuppressWarnings("unchecked")
//    public void insert(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
//        try {
//            String insertJson = new String(chain.request().httpRequest().getContent());
//            ObjectNode objectNode = JsonUtil.getObjectMapper().readValue(insertJson, new TypeReference<ObjectNode>(){});
//
//            String   table    = objectNode.get(MetaEnum.TABLE).textValue();
//            JsonNode jsonNode = objectNode.get(MetaEnum.DATA);
//
//            List<SearchDataRow> collection = new ArrayList<>();
//
//            jsonNode.forEach(node -> collection.add(SearchDataRow.buildFromJson((ObjectNode) node)));
//
//            for (SearchDataRow row : collection) {
//                ENGINE.put(table, row);
//            }
//            writeToResponseString(chain, "200");
//        } catch (ShardsOperatorException | IOException e) {
//            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
//        }
//    }

    @Rest
    public void refresh(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            String refreshJson = new String(chain.request().httpRequest().getContent());
            //todo 实现分区、分片的刷新
            NODE_SERVICE.getMasterRpcProtocol().refresh(null);
            writeToResponseString(chain, "200");
        } catch (Exception e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

    @Rest
    public void insertStat(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
        try {
            writeToResponseString(chain, NODE_SERVICE.getMasterRpcProtocol().insertStat().toString());
        } catch (AvroRemoteException e) {
            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
        }
    }

//    @Rest
//    public void test(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
//        try {
//            BufferedReader br = new BufferedReader(new FileReader("/Users/geekcat/Desktop/HUYASZ.ItemConsumeServer_yygamelivewaterlog_20171228.log"));
//            long i = 0;
//            String line;
//            while ((line = br.readLine()) != null) {
//                ItemConsumeWaterConvert serverConvert = new ItemConsumeWaterConvert();
//                ENGINE.put("item_consume_water", serverConvert.convert(1, i++, line, null));
//            }
//
//            br.close();
//            writeToResponseString(chain, "200");
//        } catch (JsonProcessingException e) {
//            writeToResponseString(chain, ExceptionUtils.getStackTrace(e));
//        } catch (ShardsOperatorException | IOException | IgnorableConvertException e) {
//            e.printStackTrace();
//        }
//    }

    @Rest
    public void insertShard(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, AvroRemoteException {
        HttpRequest request = chain.request().httpRequest();
        String serviceUrl = request.getParameterFirst("serviceUrl");
        String table = request.getParameterFirst("table");
        int shardId = Integer.parseInt(request.getParameterFirst("shardId"));
        String method = request.getParameterFirst("method");
        NODE_SERVICE.getMasterRpcProtocol().openPullTask(serviceUrl, table, shardId, method);
        writeToResponseString(chain, "200");
    }

    @Rest
    public void stopInsertShard(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, AvroRemoteException {
        HttpRequest request = chain.request().httpRequest();
        String serviceUrl = request.getParameterFirst("serviceUrl");
        String table = request.getParameterFirst("table");
        int shardId = Integer.parseInt(request.getParameterFirst("shardId"));
        NODE_SERVICE.getMasterRpcProtocol().closePullTask(serviceUrl, table, shardId);
        writeToResponseString(chain, "200");
    }

    @Rest
    public void insertShardList(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, AvroRemoteException {
        HttpRequest request = chain.request().httpRequest();
        String serviceUrl = request.getParameterFirst("serviceUrl");
        String table = request.getParameterFirst("table");
        int[] shardIdList = Stream.of(request.getParameterFirst("shardIdList").split(",")).mapToInt(Integer::parseInt).toArray();
        String method = request.getParameterFirst("method");
        for (int shardId : shardIdList) {
            NODE_SERVICE.getMasterRpcProtocol().openPullTask(serviceUrl, table, shardId, method);
        }
        writeToResponseString(chain, "200");
    }

    @Rest
    public void stopInsertShardList(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, AvroRemoteException {
        HttpRequest request = chain.request().httpRequest();
        String serviceUrl = request.getParameterFirst("serviceUrl");
        String table = request.getParameterFirst("table");
        int[] shardIdList = Stream.of(request.getParameterFirst("shardIdList").split(",")).mapToInt(Integer::parseInt).toArray();
        for (int shardId : shardIdList) {
            NODE_SERVICE.getMasterRpcProtocol().closePullTask(serviceUrl, table, shardId);
        }
        writeToResponseString(chain, "200");
    }

    @Rest
    public void syncPullTask(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, AvroRemoteException {
        HttpRequest request = chain.request().httpRequest();
        NODE_SERVICE.getMasterRpcProtocol().syncPullTask();
        writeToResponseString(chain, "200");
    }

//    @Rest
//    public void deleteShardListLock(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, IOException {
//        HttpRequest request = chain.request().httpRequest();
//        String table = request.getParameterFirst("table");
//        int[] shardIdList = Stream.of(request.getParameterFirst("shardIdList").split(",")).mapToInt(Integer::parseInt).toArray();
//        for (int shardId : shardIdList) {
//            FILE_SYSTEM.unLockShards(new ShardCoordinateContext(table, shardId));
//        }
//        writeToResponseString(chain, "200");
//    }

//    @Rest
//    public void check(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException, IOException {
//        ENGINE.check();
//        writeToResponseString(chain, "200");
//    }

    @Override
    public String getPath() {
        return "/insert";
    }
}
