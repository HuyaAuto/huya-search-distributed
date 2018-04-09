package com.huya.search.restful.http;

/** 
* @ClassName: HttpSenderContext 
* @Description: TODO(http 发送上下文) 
* @author LinYonfa
* @date Apr 26, 2016 3:01:32 PM 
* 
* @param <StdReq>
* @param <StdResp> 
*/
public class HttpSenderContext<StdReq, StdResp> extends HttpReceiverContext {

	public HttpSenderContext(HttpRequest req, HttpResponse resp) {
		super(req, resp);
	}

}