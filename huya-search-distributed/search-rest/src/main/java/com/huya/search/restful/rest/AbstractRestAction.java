package com.huya.search.restful.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.huya.search.restful.base.InterceptorChainContext;
import com.huya.search.restful.config.ApiConfig;
import com.huya.search.restful.http.HttpRequest;
import com.huya.search.restful.http.HttpResponse;
import com.huya.search.util.JsonUtil;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.util.*;

/** 
* @ClassName: AbstractRestAction 
* @Description: TODO(RestAction抽象实现) 
* @author LinYonfa
* @date Apr 26, 2016 3:07:10 PM 
*  
*/
public abstract class AbstractRestAction implements RestAction {
	
	/**
	 * 默认按照配置的普通token进行验证
	 */
	@Override
	public boolean auth(InterceptorChainContext<RestRequest, RestResponse> chain) throws ActionException {
		RestRequest adminReq = chain.request();
		ApiConfig config = ApiConfig.instance();
		String token = config.getToken();

		if (token.equals(adminReq.getToken()))
			return true;

		throw new ActionException(String.format("request action auth fail, token is error !"));
	}

	/**
	 * 解析http get 请求参数
	 * 
	 * @param chain
	 * @param key
	 * @throws ActionException
	 */
	protected List<String> parseParamGet(InterceptorChainContext<RestRequest, RestResponse> chain, String key) {
		HttpRequest httpReq = chain.request().httpRequest();
		return httpReq.getParameter(key);
	}

	/**
	 * 解析http get 请求参数
	 * 
	 * @param chain
	 * @param key
	 * @throws ActionException
	 */
	protected String parseParamGetFirst(InterceptorChainContext<RestRequest, RestResponse> chain, String key) {
		HttpRequest httpReq = chain.request().httpRequest();
		return httpReq.getParameterFirst(key);
	}

	/**
	 * 解析http post 请求参数
	 * 
	 * @param chain
	 * @return url
	 * @throws ActionException
	 */
	protected List<Object> parseParamPost(InterceptorChainContext<RestRequest, RestResponse> chain) {
		HttpRequest httpReq = chain.request().httpRequest();
		try {
			return JsonUtil.getObjectMapper().readValue(new String(httpReq.getContent()), new TypeReference<List<Object>>(){});
		} catch (IOException e) {
			return new ArrayList<>();
		}
	}

	/**
	 * 解析http post 请求参数
	 * 
	 * @param chain
	 * @param key
	 * @return url
	 * @throws ActionException
	 */
	@SuppressWarnings({ "rawtypes" })
	protected List<String> parseParamPost(InterceptorChainContext<RestRequest, RestResponse> chain, String key) {
		HttpRequest httpReq = chain.request().httpRequest();
		List<String> paramList = Lists.newArrayList();
		try {
			Map map = JsonUtil.getObjectMapper().readValue(new String(httpReq.getContent()), Map.class);
			Object obj = map.get(key);
			if (obj instanceof List) {
				List t = (List) obj;
				for (Object o : t) {
					paramList.add(o.toString());
				}
			} else {
				paramList.add(obj.toString());
			}
		} catch (Exception ignored) {}

		return paramList;
	}

	/**
	 * 解析http post 请求参数
	 * 
	 * @param chain
	 * @param key
	 * @return url
	 * @throws ActionException
	 */
	protected Object parseParamPostJson(InterceptorChainContext<RestRequest, RestResponse> chain, String key) throws ActionException {
		HttpRequest httpReq = chain.request().httpRequest();
		try {
			Map<?, ?> map = JsonUtil.getObjectMapper().readValue(new String(httpReq.getContent()), Map.class);
			return map.get(key);
		} catch (Exception e) {
			throw new ActionException("parse post paramter err", e);
		}
	}

	/**
	 * 写响应
	 * 
	 * @param chain
	 * @return url
	 */
	protected void writeToResponseString(InterceptorChainContext<RestRequest, RestResponse> chain, String obj) {
		HttpResponse httpResp = chain.response().httpResponse();
		httpResp.setStatus(HttpResponse.OK);
		if (obj != null)
			httpResp.setContent(obj.getBytes());
	}

	/**
	 * 写响应
	 * 
	 * @param chain
	 * @return url
	 */
	protected void writeToResponseJson(InterceptorChainContext<RestRequest, RestResponse> chain, Object obj) {
		try {
			HttpResponse httpResp = chain.response().httpResponse();
			String content = JsonUtil.getObjectMapper().writeValueAsString(obj);
			httpResp.setStatus(HttpResponse.OK);
			httpResp.setContent(content.getBytes());
		} catch (Exception ignored) {}
	}

	/**
	 * 写响应
	 * 
	 * @param chain
	 * @return url
	 */
	protected void writeToResponseHtml(InterceptorChainContext<RestRequest, RestResponse> chain, Object obj) {
		HttpResponse httpResp = chain.response().httpResponse();
		if (obj != null) {
			httpResp.setStatus(HttpResponse.OK);
			httpResp.setContentType(HttpResponse.CONTENT_TYPE_HTML);
			httpResp.setContent(obj.toString().getBytes());
		}
	}


	protected void reportError(InterceptorChainContext<RestRequest, RestResponse> chain, Exception e) {
		ObjectNode objectNode = new ObjectNode(JsonUtil.getObjectMapper().getNodeFactory());
		String errMsg = ExceptionUtils.getStackTrace(e);
		if (errMsg.length() > 200) {
			errMsg = errMsg.substring(0, 200);
		}
		objectNode.put("errMsg", errMsg);
		objectNode.put("code", 500);
		writeToResponseString(chain, objectNode.toString());
	}


	/**
	 * token有效性结果
	 * 
	 * @author kewn
	 *
	 */
	public enum TokenState {
		Null,				// 无token
		Invalid,			// 无效
		Valid,				// 有效
		Accepted,			// 已处理
	}
}
