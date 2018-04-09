package com.huya.search.restful.base;

/**
 * 事件源
 * 
 * @author kewn
 *
 */
public abstract class EventContext<Req extends UserRequest, Resp extends UserResponse> {

	private Req req;
	private Resp resp;

	public EventContext(Req req, Resp resp) {
		this.req = req;
		this.resp = resp;
	}

	/**
	 * 事件源-请求
	 * 
	 * @return
	 */
	public Req getRequest() {
		return this.req;
	}

	/**
	 * 事件源响应
	 * 
	 * @return
	 */
	public Resp getResponse() {
		return this.resp;
	}
}
