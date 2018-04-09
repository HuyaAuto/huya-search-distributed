package com.huya.search.restful.base;

import java.util.Iterator;

public class InterceptorChainContext<Req extends UserRequest, Resp extends UserResponse> {

	private Req req;
	private Resp resp;
	private InterceptorController<Req, Resp> controller;

	private Iterator<? extends Interceptor<Req, Resp>> it = null;

	private InterceptorChainContext(InterceptorController<Req, Resp> controller, Req request, Resp response) {
		this.controller = controller;
		this.req = request;
		this.resp = response;
		if (null != controller.getInterceptors()) {
			this.it = controller.getInterceptors().iterator();
		}
	}

	public void proceed() {
		if (null != it && it.hasNext()) {
			it.next().execute(this);
		}
	}

	public Req request() {
		return req;
	}

	public Resp response() {
		return resp;
	}

	static <Req extends UserRequest, Resp extends UserResponse> InterceptorChainContext<Req, Resp>
	create(InterceptorController<Req, Resp> controller, Req request, Resp response) {
		InterceptorChainContext<Req, Resp> chain = new InterceptorChainContext<>(controller, request, response);
		return chain;
	}
}
