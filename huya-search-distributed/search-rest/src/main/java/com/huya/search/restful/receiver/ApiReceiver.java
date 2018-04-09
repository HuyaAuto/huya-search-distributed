package com.huya.search.restful.receiver;

import com.huya.search.restful.controller.ApiController;
import com.huya.search.restful.http.HttpReceiver;
import com.huya.search.restful.http.HttpReceiverContext;
import com.huya.search.restful.rest.RestRequest;
import com.huya.search.restful.rest.RestResponse;

/**
 * 
 * @author kewn
 *
 */
public class ApiReceiver extends HttpReceiver<ApiController, HttpReceiverContext> {

	public ApiReceiver(ApiController controller) {
		super(controller);
	}

	@Override
	public void receive(HttpReceiverContext context) {
		RestRequest adminReq = new RestRequest(context.httpRequest());
		RestResponse adminResp = new RestResponse(context.httpResponse());

		controller().onRequest(adminReq, adminResp);
	}

	@Override
	public String getPath() {
		return "/";
	}

}