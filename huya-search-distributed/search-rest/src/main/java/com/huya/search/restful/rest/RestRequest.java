package com.huya.search.restful.rest;

import com.huya.search.restful.base.UserRequest;
import com.huya.search.restful.http.HttpRequest;

public class RestRequest extends UserRequest {
	
	private HttpRequest httpRequest;
	
	private static final String TOKEN = "token";
	
	private String token;

	public RestRequest(HttpRequest httpRequest) {
		super();
		this.httpRequest = httpRequest;
		token = httpRequest.getParameterFirst(TOKEN);
	}

	String getToken() {
		return token;
	}
	
	public HttpRequest httpRequest() {
		return httpRequest;
	}
}