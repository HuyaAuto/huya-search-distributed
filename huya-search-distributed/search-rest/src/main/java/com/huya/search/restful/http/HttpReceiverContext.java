package com.huya.search.restful.http;

import com.huya.search.restful.base.RequestReceiverContext;

import java.util.HashMap;
import java.util.Map;

/** 
* @ClassName: HttpReceiverContext 
* @Description: TODO(http 请求上下文信息) 
* @author LinYonfa
* @date Apr 26, 2016 3:02:56 PM 
*  
*/
public class HttpReceiverContext extends RequestReceiverContext {

	/**
	 * 请求处理设置相关属性
	 */
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * http 请求
	 */
	private HttpRequest request;

	private HttpResponse response;

	public HttpReceiverContext(HttpRequest req, HttpResponse resp) {
		super(req.getPath());
		this.request = req;
		this.response = resp;
	}

	/**
	 * 相关存储属性值
	 */
	public Map<String, Object> attributes() {
		return attributes;
	}
	
	public void putAttribute(String key, Object obj){
		this.attributes.put(key, obj);
	}
	
	public Object getAttribute(String key){
		return this.attributes.get(key);
	}

	/**
	 * get current http request
	 * 
	 * @return
	 */
	public HttpRequest httpRequest() {
		return request;
	}

	/**
	 * get current http response
	 */
	public HttpResponse httpResponse() {
		return response;
	}
}
