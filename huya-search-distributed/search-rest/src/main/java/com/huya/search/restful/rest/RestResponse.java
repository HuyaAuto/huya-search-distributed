package com.huya.search.restful.rest;

import com.huya.search.restful.base.UserResponse;
import com.huya.search.restful.http.HttpResponse;

public class RestResponse extends UserResponse {
	
	private HttpResponse response;
	
	public RestResponse(HttpResponse response) {
		super();
		this.response = response;
	}
	
	public HttpResponse httpResponse() {
		return response;
	}

}
