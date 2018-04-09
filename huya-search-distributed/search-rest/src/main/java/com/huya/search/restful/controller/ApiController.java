package com.huya.search.restful.controller;


import com.huya.search.restful.base.Interceptor;
import com.huya.search.restful.base.StandardInterceptorController;
import com.huya.search.restful.rest.RestRequest;
import com.huya.search.restful.rest.RestResponse;

import java.util.List;

public class ApiController extends StandardInterceptorController<RestRequest, RestResponse> {

	public ApiController(List<? extends Interceptor<RestRequest, RestResponse>> interceptors) {
		super(interceptors);
	}
}
