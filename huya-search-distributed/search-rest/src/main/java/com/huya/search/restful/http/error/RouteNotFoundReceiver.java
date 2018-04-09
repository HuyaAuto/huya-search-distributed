package com.huya.search.restful.http.error;

import com.huya.search.restful.http.HttpReceiver;
import com.huya.search.restful.http.HttpReceiverContext;
import com.huya.search.restful.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ClassName: RouteNotFoundReceiver 
* @Description: TODO(路由找不到处理Receiver) 
* @author LinYonfa
* @date Apr 26, 2016 3:00:32 PM 
*  
*/
public class RouteNotFoundReceiver extends HttpReceiver<ErrorController, HttpReceiverContext> {

	public RouteNotFoundReceiver() {
		super(new ErrorController());
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void receive(HttpReceiverContext context) {

		String content = "request resource is not found";

		context.httpResponse().setContent(content);
		context.httpResponse().setStatus(HttpResponse.NOT_FOUND);

		logger.info("route for request path {} is not found", context.httpRequest().getPath());
	}

	@Override
	public String getPath() {
		return null;
	}
}
