package com.huya.search.restful.http.error;

import com.huya.search.restful.http.HttpReceiver;
import com.huya.search.restful.http.HttpReceiverContext;
import com.huya.search.restful.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @ClassName: MethodNotAllowedReceiver 
* @Description: TODO(不允许方法处理) 
* @author LinYonfa
* @date Apr 26, 2016 3:01:02 PM 
*  
*/
public class MethodNotAllowedReceiver extends HttpReceiver<ErrorController, HttpReceiverContext> {

	public MethodNotAllowedReceiver() {
		super(new ErrorController());
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void receive(HttpReceiverContext context) {

		String content = "http method is not allowed";

		context.httpResponse().setStatus(HttpResponse.METHOD_NOT_ALLOWED);
		context.httpResponse().setContent(content);

		logger.info("invalid http method {} for path {}", context.httpRequest().getMethod(), context.httpRequest().getPath());
	}

	@Override
	public String getPath() {
		return null;
	}

}